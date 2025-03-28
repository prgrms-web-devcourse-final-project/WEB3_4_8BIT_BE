package com.backend.global.auth.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.backend.domain.member.entity.Members;
import com.backend.domain.member.exception.MembersErrorCode;
import com.backend.domain.member.exception.MembersException;
import com.backend.domain.member.repository.MembersRepository;
import com.backend.global.auth.exception.JwtAuthenticationErrorCode;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.auth.oauth2.CustomOAuth2User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;
	private final MembersRepository membersRepository;

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expire-time-seconds}")
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String refreshAccessToken(String expiredToken) {
		Claims claims = parseClaims(expiredToken);

		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

		return Jwts.builder()
			.subject(claims.getSubject())
			.claim("auth", claims.get("auth"))
			.claim("email", claims.get("email"))
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	public void addToBlacklist(String token) {
		try {
			Claims claims = parseClaims(token);
			long expiration = claims.getExpiration().getTime(); // 토큰 만료 시간
			long now = System.currentTimeMillis(); // 현재 시간
			long remainingTime = (expiration - now) / 1000; // 남은 시간

			if (remainingTime > 0) {
				redisTemplate.opsForValue()
					.set("BL:" + token, "blacklisted", remainingTime, TimeUnit.SECONDS);
				log.debug("Token added to blacklist, expires in {} seconds", remainingTime);
			} else {
				log.debug("Token is already expired, skipping blacklist");
			}
		} catch (ExpiredJwtException e) {
			log.debug("Token is expired, skipping blacklist");
		} catch (Exception e) {
			log.error("Failed to add token to blacklist", e);
			throw new JwtAuthenticationException((JwtAuthenticationErrorCode.BLACK_LIST_FAIL));
		}
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
	}

	public String createAccessToken(Authentication authentication) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		return Jwts.builder()
			.subject(String.valueOf(user.getId())) // 식별자로 id만 사용
			.claim("auth", getAuthorities(authentication)) // 권한 정보
			.claim("email", user.getEmail()) // email claim 추가
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	public String createRefreshToken() {
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshTokenValidityInSeconds * 1000);

		return Jwts.builder()
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);  // 수정된 parseClaims 사용
		Long userId = Long.valueOf(claims.getSubject());

		Members members = membersRepository.findById(userId)
			.orElseThrow(() -> new MembersException(MembersErrorCode.MEMBER_NOT_FOUND));


		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		CustomOAuth2User principal = new CustomOAuth2User(
			authorities, // 권한 정보
			Map.of(
				"id", members.getMemberId(),
				"email", members.getEmail()
			), // OAuth2 속성
			"id", // nameAttributeKey
			members.getMemberId(), // id
			members.getEmail() // email
		);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_SIGNATURE);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token: {}", e.getMessage());
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.EXPIRED_TOKEN);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token: {}", e.getMessage());
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.UNSUPPORTED_TOKEN);
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.EMPTY_CLAIMS);
		}

	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이어도 Claims 반환
			return e.getClaims();
		} catch (Exception e) {
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
		}
	}

	public Long getUserIdFromExpiredToken(String token) {
		try {
			return Long.valueOf(parseClaims(token).getSubject());
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이어도 서명이 유효하면 ID 반환
			return Long.valueOf(e.getClaims().getSubject());
		} catch (Exception e) {
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * Authentication 객체에서 권한 정보 추출
	 * @param authentication 인증 객체
	 * @return 쉼표로 구분된 권한 문자열
	 */
	private String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

	/**
	 * refreshtoken이 만료여부 확인하는 메서드
	 *
	 * @param token refresh token
	 * @return true: 만료되었거나 유효하지 않은 경우 / false: 아직 유효한 경우
	 */
	public boolean isRefreshTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return true; // 파싱 오류 = 만료 또는 유효하지 않음
		}
	}
}


package com.backend.global.auth.jwt;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CookieUtil cookieUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/swagger-ui")
			|| path.startsWith("/v3/api-docs")
			|| path.startsWith("/swagger-resources")
			|| path.startsWith("/webjars");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) {

		try {
			// 쿠키에서 JWT 토큰 추출
			String accessToken = cookieUtil.extractTokenFromCookie(request);
			log.debug("JwtAuthenticationFilter - 쿠키에서 accessToken 추출: {}", accessToken);

			if (accessToken != null) {
				// Access Token 블랙리스트 확인
				if (jwtTokenProvider.isBlacklisted(accessToken)) {
					log.warn("JwtAuthenticationFilter - 블랙리스트에 등록된 accessToken");
					refreshAndContinue(request, response, filterChain, accessToken);
					return;
				}

				try {
					if (jwtTokenProvider.validateToken(accessToken)) {
						Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
						SecurityContextHolder.getContext().setAuthentication(authentication);
						log.debug("사용자 '{}'의 인증 정보를 security context에 설정함", authentication.getName());
					}
				} catch (JwtAuthenticationException e) {
					refreshAndContinue(request, response, filterChain, accessToken);
					return;
				}
			}

			filterChain.doFilter(request, response);

		} catch (JwtAuthenticationException e) {
			log.warn("JWT 인증 예외 발생: {}", e.getMessage());
			throw e;

		} catch (Exception e) {
			log.warn("사용자 인증 정보를 설정할 수 없음: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void refreshAndContinue(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain, String expiredAccessToken) {
		try {
			Long userId = jwtTokenProvider.getUserIdFromExpiredToken(expiredAccessToken);
			String refreshTokenKey = "RT:" + userId;
			String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

			if (refreshToken == null || jwtTokenProvider.isRefreshTokenExpired(refreshToken)) {
				log.warn("RefreshToken 만료 또는 없음");

				ResponseCookie logoutCookie = cookieUtil.createLogoutCookie();
				response.addHeader(HttpHeaders.SET_COOKIE, logoutCookie.toString());

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			// refresh token ttl갱신
			redisTemplate.expire(refreshTokenKey, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
			log.debug("기존 RefreshToken TTL만 갱신");

			// access token 재발급
			String newAccessToken = jwtTokenProvider.refreshAccessToken(expiredAccessToken);
			ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(newAccessToken);
			response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

			// SecurityContext 설정
			Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("재발급 및 인증 완료: {}", authentication.getName());

			// 다음 필터로 이동
			filterChain.doFilter(request, response);

		} catch (Exception e) {
			log.error("refreshAndContinue 중 예외 발생: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
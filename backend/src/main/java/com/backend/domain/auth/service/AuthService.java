package com.backend.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backend.global.auth.exception.JwtAuthenticationErrorCode;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.auth.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 만료된 Access Token을 검증 후, 유효한 Refresh Token이 존재하면 새로운 Access Token을 발급하는 메서드
	 *
	 * @param accessToken 만료된 Access Token
	 * @return 새로 발급된 Access Token
	 * @throws JwtAuthenticationException 블랙리스트 토큰이거나 Refresh Token이 없을 경우 예외 발생
	 */
	public String refreshAccessToken(String accessToken) {
		validateAccessToken(accessToken);

		Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);

		validateRefreshToken(userId);

		String newAccessToken = jwtTokenProvider.refreshAccessToken(accessToken);
		log.debug("New access token created for userId: {}", userId);

		jwtTokenProvider.addToBlacklist(accessToken);

		log.debug("새 토큰 발급: userId={}", userId);
		return newAccessToken;
	}

	/**
	 * 로그아웃 처리 메서드
	 *
	 * @param accessToken 사용자의 기존 Access Token
	 */
	public void logout(String accessToken) {
		validateAccessToken(accessToken);

		Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);
		jwtTokenProvider.addToBlacklist(accessToken);

		redisTemplate.delete("RT:" + userId);
	}

	/**
	 * Access Token이 블랙리스트에 포함되어 있는지 확인하는 메서드
	 *
	 * @param accessToken 검증 토큰
	 * @throws JwtAuthenticationException 블랙리스트에 존재할 경우 예외 발생
	 */
	private void validateAccessToken(String accessToken) {
		if (jwtTokenProvider.isBlacklisted(accessToken)) {
			log.warn("블랙리스트된 토큰 사용");
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * Redis에 저장된 Refresh Token의 존재 여부를 확인하는 메서드
	 *
	 * @param userId 사용자 ID
	 * @throws JwtAuthenticationException Refresh Token이 없을 경우 예외 발생
	 */
	private void validateRefreshToken(Long userId) {
		String refreshToken = redisTemplate.opsForValue().get("RT:" + userId);
		if (refreshToken == null) {
			log.warn("RefreshToken 없음: userId={}", userId);
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
		}
	}
}

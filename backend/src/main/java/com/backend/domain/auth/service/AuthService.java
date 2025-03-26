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

	public void logout(String accessToken) {
		validateAccessToken(accessToken);

		Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);
		jwtTokenProvider.addToBlacklist(accessToken);

		redisTemplate.delete("RT:" + userId);
	}

	// AccessToken 블랙리스트 체크
	private void validateAccessToken(String accessToken) {
		if (jwtTokenProvider.isBlacklisted(accessToken)) {
			log.warn("블랙리스트된 토큰 사용");
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
		}
	}

	// RefreshToken Redis에서 유효한지 체크
	private void validateRefreshToken(Long userId) {
		String refreshToken = redisTemplate.opsForValue().get("RT:" + userId);
		if (refreshToken == null) {
			log.warn("RefreshToken 없음: userId={}", userId);
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
		}
	}
}

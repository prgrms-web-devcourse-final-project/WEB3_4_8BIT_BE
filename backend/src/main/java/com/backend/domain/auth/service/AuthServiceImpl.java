package com.backend.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.backend.global.auth.exception.JwtAuthenticationErrorCode;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.auth.jwt.JwtTokenProvider;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final CookieUtil cookieUtil;

	@Override
	public void logout(final HttpServletRequest request, final HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
		}

		validateAccessToken(accessToken);

		Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);
		jwtTokenProvider.addToBlacklist(accessToken);
		redisTemplate.delete("RT:" + userId);

		ResponseCookie cookie = cookieUtil.createLogoutCookie();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		log.debug("로그아웃 처리 완료 - userId: {}", userId);
	}

	/**
	 * Access Token이 블랙리스트에 포함되어 있는지 확인하는 메서드
	 *
	 * @param accessToken 검증 토큰
	 * @throws JwtAuthenticationException 블랙리스트에 존재할 경우 예외 발생
	 */
	private void validateAccessToken(final String accessToken) {
		if (jwtTokenProvider.isBlacklisted(accessToken)) {
			log.warn("블랙리스트된 토큰 사용");
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.INVALID_TOKEN);
		}
	}
}

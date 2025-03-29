package com.backend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieUtil {

	@Value("${cookie.httpOnly}")
	private boolean httpOnly;

	@Value("${cookie.secure}")
	private boolean secure;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	public String extractTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					log.debug("쿠키에서 accessToken 추출: {}", cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		log.debug("쿠키에서 accessToken 추출 못함");
		return null;
	}

	public ResponseCookie createAccessTokenCookie(String token) {
		ResponseCookie cookie = ResponseCookie.from("accessToken", token)
			.httpOnly(httpOnly)
			.secure(secure)
			.sameSite("Lax")
			.path("/")
			.maxAge(refreshTokenValidityInSeconds)
			.build();

		log.debug("Access Token 쿠키 생성: {}", cookie);
		return cookie;
	}

	public ResponseCookie createLogoutCookie() {
		return ResponseCookie.from("accessToken", "")
			.httpOnly(httpOnly)
			.secure(secure)
			.sameSite("Lax")
			.path("/")
			.maxAge(0)
			.build();
	}
}

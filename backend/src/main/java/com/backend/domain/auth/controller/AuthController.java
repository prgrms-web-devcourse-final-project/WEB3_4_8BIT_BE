package com.backend.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.auth.service.AuthService;
import com.backend.global.auth.exception.JwtAuthenticationErrorCode;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.response.GenericResponse;
import com.backend.global.util.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/refresh")
	@Operation(summary = "AccessToken 재발급하기", description = "AccessToken 만료시 재발급해주는 API")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/refresh)");
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
		}

		try {
			String newAccessToken = authService.refreshAccessToken(accessToken);

			// 새로운 Access Token을 쿠키에 저장
			ResponseCookie cookie = cookieUtil.createAccessTokenCookie(newAccessToken);
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			log.debug("토큰 갱신 성공");
		} catch (Exception e) {
			log.warn("AccessToken refresh failed: {}", e.getMessage());
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.REFRESH_FAIL);
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃하기", description = "로그아웃시 쿠키 삭제 및 블랙리스트 처리 API")
	public GenericResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/logout)");
			throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
		}

		try {
			authService.logout(accessToken);
			// 쿠키 삭제
			ResponseCookie emptyCookie = cookieUtil.createLogoutCookie();
			response.addHeader(HttpHeaders.SET_COOKIE, emptyCookie.toString());

			log.debug("로그아웃 성공");
			return GenericResponse.ok("로그아웃이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("로그아웃 실패: {}", e.getMessage());
			return GenericResponse.fail("로그아웃 실패");
		}
	}
}
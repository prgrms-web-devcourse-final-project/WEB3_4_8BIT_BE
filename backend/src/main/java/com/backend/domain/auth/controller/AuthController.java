package com.backend.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.auth.service.AuthService;
import com.backend.global.auth.exception.JwtAuthenticationException;
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
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthController {

	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/refresh")
	@Operation(summary = "AccessToken 재발급")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/refresh)");
			throw new JwtAuthenticationException("토큰이 존재하지 않습니다.");
		}

		try {
			String newAccessToken = authService.refreshAccessToken(accessToken);

			// 새로운 Access Token을 쿠키에 저장
			ResponseCookie cookie = cookieUtil.createAccessTokenCookie(newAccessToken);
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			log.debug("토큰 갱신 성공");
		} catch (Exception e) {
			log.warn("AccessToken refresh failed: {}", e.getMessage());
			throw new JwtAuthenticationException("토큰 갱신 실패: " + e.getMessage());
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/logout)");
			throw new JwtAuthenticationException("토큰이 존재하지 않습니다.");
		}

		try {
			authService.logout(accessToken);
			// 쿠키 삭제
			ResponseCookie emptyCookie = cookieUtil.createLogoutCookie();
			response.addHeader(HttpHeaders.SET_COOKIE, emptyCookie.toString());

			log.debug("로그아웃 성공");
			return ResponseEntity.ok("로그아웃이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("로그아웃 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 실패");
		}
	}
}
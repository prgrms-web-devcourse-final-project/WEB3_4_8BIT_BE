package com.backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.auth.service.AuthService;
import com.backend.global.dto.response.GenericResponse;

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

	@PostMapping("/refresh")
	@Operation(summary = "AccessToken 재발급하기", description = "AccessToken 만료시 재발급해주는 API")
	public ResponseEntity<GenericResponse<Void>> refreshToken(HttpServletRequest request,
		HttpServletResponse response) {
		authService.refreshAccessToken(request, response);
		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃하기", description = "로그아웃시 쿠키 삭제 및 블랙리스트 처리 API")
	public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request,
		HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.ok(GenericResponse.of(true));
	}
}
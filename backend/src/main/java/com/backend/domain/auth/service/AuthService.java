package com.backend.domain.auth.service;

import com.backend.global.auth.exception.JwtAuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	/**
	 * 로그아웃 처리 메서드
	 *
	 * @param request  클라이언트의 HTTP 요청 (쿠키에서 Access Token 추출)
	 * @param response 클라이언트의 HTTP 응답 (Access Token 쿠키 제거용)
	 * @throws JwtAuthenticationException Access Token이 누락되었거나 블랙리스트에 있는 경우 발생
	 * @implSpec Access Token을 검증한 뒤 블랙리스트에 등록하고, Refresh Token을 Redis에서 제거하며 쿠키도 만료시킨다.
	 */
	void logout(final HttpServletRequest request, final HttpServletResponse response);
}
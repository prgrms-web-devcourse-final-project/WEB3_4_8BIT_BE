package com.backend.domain.auth.service;

import com.backend.global.auth.exception.JwtAuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	/**
	 * AccessToken 재발급 메서드
	 *
	 * @param request  쿠키에서 AccessToken을 추출
	 * @param response 재발급된 AccessToken을 쿠키에 추가
	 * @throws JwtAuthenticationException 유효하지 않거나 블랙리스트된 토큰, RefreshToken 없음 등의 인증 오류 발생 시
	 * @implSpec 쿠키에서 AccessToken을 가져와 유효성을 검사하고, 유효한 RefreshToken이 존재하면 새 토큰을 발급 후 쿠키에 설정한다.
	 */
	void refreshAccessToken(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 로그아웃 처리 메서드
	 *
	 * @param request  클라이언트의 HTTP 요청 (쿠키에서 Access Token 추출)
	 * @param response 클라이언트의 HTTP 응답 (Access Token 쿠키 제거용)
	 * @throws JwtAuthenticationException Access Token이 누락되었거나 블랙리스트에 있는 경우 발생
	 * @implSpec Access Token을 검증한 뒤 블랙리스트에 등록하고, Refresh Token을 Redis에서 제거하며 쿠키도 만료시킨다.
	 */
	void logout(HttpServletRequest request, HttpServletResponse response);
}
package com.backend.domain.auth.service;

public interface AuthService {

	/**
	 * 만료된 Access Token을 검증 후, 유효한 Refresh Token이 존재하면 새로운 Access Token을 발급하는 메서드
	 *
	 * @param accessToken 만료된 Access Token
	 * @return 새로 발급된 Access Token
	 */
	String refreshAccessToken(String accessToken);

	/**
	 * 로그아웃 처리 메서드
	 *
	 * @param accessToken 사용자의 기존 Access Token
	 */
	void logout(String accessToken);
}
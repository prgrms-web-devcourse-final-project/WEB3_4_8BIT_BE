package com.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends RuntimeException {

	private final JwtAuthenticationErrorCode jwtAuthenticationErrorCode;

	public JwtAuthenticationException(JwtAuthenticationErrorCode jwtAuthenticationErrorCode) {
		super(jwtAuthenticationErrorCode.getMessage());
		this.jwtAuthenticationErrorCode = jwtAuthenticationErrorCode;
	}

	public HttpStatus getStatus() {
		return jwtAuthenticationErrorCode.getHttpStatus();
	}
}
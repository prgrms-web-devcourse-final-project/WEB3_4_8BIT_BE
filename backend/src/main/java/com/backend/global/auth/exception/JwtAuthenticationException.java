package com.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends RuntimeException {

	private final HttpStatus status;

	public JwtAuthenticationException(String message) {
		this(message, HttpStatus.UNAUTHORIZED);
	}

	public JwtAuthenticationException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
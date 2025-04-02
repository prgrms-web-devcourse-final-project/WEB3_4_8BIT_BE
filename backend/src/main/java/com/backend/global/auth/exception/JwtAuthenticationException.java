package com.backend.global.auth.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends GlobalException {

	private final ErrorCode errorCode;

	public JwtAuthenticationException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public JwtAuthenticationException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}
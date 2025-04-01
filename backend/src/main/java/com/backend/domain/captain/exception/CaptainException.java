package com.backend.domain.captain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CaptainException extends RuntimeException {
	private final CaptainErrorCode captainErrorCode;

	public CaptainException(CaptainErrorCode captainErrorCode) {
		super(captainErrorCode.getMessage());
		this.captainErrorCode = captainErrorCode;
	}

	public HttpStatus getStatus() {
		return captainErrorCode.getHttpStatus();
	}
}

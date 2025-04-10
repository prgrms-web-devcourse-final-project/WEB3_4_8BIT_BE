package com.backend.domain.captain.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class CaptainException extends GlobalException {
	private final ErrorCode errorCode;

	public CaptainException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public CaptainException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

package com.backend.domain.fishpoint.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class FishPointException extends GlobalException {
	private final ErrorCode errorCode;

	public FishPointException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public FishPointException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

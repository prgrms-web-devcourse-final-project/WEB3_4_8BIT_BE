package com.backend.domain.fish.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class FishException extends GlobalException {

	private final ErrorCode errorCode;

	public FishException(final ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public FishException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}
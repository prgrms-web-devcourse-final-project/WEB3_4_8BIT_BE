package com.backend.domain.fishingtrippost.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class FishingTripPostException extends GlobalException {
	private final ErrorCode errorCode;

	public FishingTripPostException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public FishingTripPostException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

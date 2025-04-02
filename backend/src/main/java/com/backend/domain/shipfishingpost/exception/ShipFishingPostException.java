package com.backend.domain.shipfishingpost.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class ShipFishingPostException extends GlobalException {

	private final ErrorCode errorCode;
	private Throwable cause;

	public ShipFishingPostException(final ShipFishingPostErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public ShipFishingPostException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
		this.cause = cause;
	}
}
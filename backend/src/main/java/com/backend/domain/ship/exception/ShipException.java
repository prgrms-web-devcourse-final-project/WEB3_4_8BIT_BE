package com.backend.domain.ship.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class ShipException extends GlobalException {

	private final ErrorCode errorCode;

	public ShipException(final ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public ShipException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

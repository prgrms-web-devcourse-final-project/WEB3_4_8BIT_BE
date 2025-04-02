package com.backend.domain.reservation.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class ReservationException extends GlobalException {

	private final ErrorCode errorCode;

	public ReservationException(final ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public ReservationException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

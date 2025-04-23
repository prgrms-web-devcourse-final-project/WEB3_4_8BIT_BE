package com.backend.global.payment.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class PaymentException extends GlobalException {

	private final ErrorCode errorCode;

	public PaymentException(final ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public PaymentException(final Throwable cause, final ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

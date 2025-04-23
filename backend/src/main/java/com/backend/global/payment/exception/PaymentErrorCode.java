package com.backend.global.payment.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

	TOSS_API_ERROR(HttpStatus.BAD_REQUEST, 16001, "결제 오류가 발생했습니다."),
	TOSS_API_CANCEL_FAILED(HttpStatus.BAD_REQUEST, 16002, "결제 취소 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

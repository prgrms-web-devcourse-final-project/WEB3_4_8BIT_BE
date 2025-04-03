package com.backend.domain.reservation.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, 11001, "예약 정보가 존재 하지 않습니다."),
	WRONG_PRICE_VALUE(HttpStatus.BAD_REQUEST, 11002, "예약 금액이 일치하지 않습니다."),
	NOT_AVAILABLE_DATE_RESERVATION(HttpStatus.CONFLICT, 11003, "예약 가능한 일자가 아닙니다."),
	NOT_AVAILABLE_END_RESERVATION(HttpStatus.CONFLICT, 11004, "예약 정원 초과 입니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

package com.backend.domain.reservation.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode {

	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, 11001, "예약 정보가 존재 하지 않습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}

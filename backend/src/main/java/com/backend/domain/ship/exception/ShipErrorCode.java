package com.backend.domain.ship.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipErrorCode {

	SHIP_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "선박 정보가 존재하지 않음"),
	SHIP_MISMATCH_MEMBER_ID(HttpStatus.BAD_REQUEST, 8002, "선박 소유자 정보가 일치하지 않음");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}

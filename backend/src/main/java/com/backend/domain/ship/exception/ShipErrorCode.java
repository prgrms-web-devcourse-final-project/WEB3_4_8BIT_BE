package com.backend.domain.ship.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipErrorCode implements ErrorCode {

	SHIP_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "선박 정보가 존재하지 않음"),
	SHIP_MISMATCH_MEMBER_ID(HttpStatus.BAD_REQUEST, 8002, "선박 소유자 정보가 일치하지 않음"),
	MAX_SHIP_COUNT_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, 8003, "선박은 최대 5개까지만 등록 가능합니다."),
	SHIP_UNAUTHORIZED_AUTHOR(HttpStatus.FORBIDDEN, 8004, "해당 선박의 소유자가 아닙니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

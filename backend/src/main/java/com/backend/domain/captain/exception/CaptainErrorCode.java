package com.backend.domain.captain.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaptainErrorCode implements ErrorCode {

	//선장 에러 코드 200
	CAPTAIN_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "해당 선장을 찾을 수 없습니다."),
	NOT_CAPTAIN(HttpStatus.FORBIDDEN, 2002, "해당 회원은 선장이 아닙니다."),
	;

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

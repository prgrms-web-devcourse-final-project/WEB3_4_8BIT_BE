package com.backend.domain.captain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaptainErrorCode {

	//선장 에러 코드 200
	CAPTAIN_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "해당 선장을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}

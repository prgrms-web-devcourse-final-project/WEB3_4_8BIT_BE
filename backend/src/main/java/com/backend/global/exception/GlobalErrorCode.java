package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 *
 * @author Kim Dong O
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

	//공통 서버 에러 코드 500
	NOT_VALID(HttpStatus.BAD_REQUEST, 5001, "요청하신 유효성 검증에 실패하였습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "서버 내부 오류가 발생하였습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
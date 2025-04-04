package com.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * GlobalErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 *
 * @author Kim Dong O
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

	//공통 서버 에러 코드 500
	NOT_VALID(HttpStatus.BAD_REQUEST, 5001, "요청하신 유효성 검증에 실패하였습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "서버 내부 오류가 발생하였습니다."),
	WRONG_SORT_CONDITION(HttpStatus.BAD_REQUEST, 5003, "지원하지 않는 정렬 항목입니다."),
	REPOSITORY_FORMAT_PARSE_ERROR(HttpStatus.BAD_REQUEST, 5004, "파라미터 타입이 잘못됐습니다."),
	DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, 5005, "데이터 무결성 오류가 발생했습니다. (중복 데이터 등)");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
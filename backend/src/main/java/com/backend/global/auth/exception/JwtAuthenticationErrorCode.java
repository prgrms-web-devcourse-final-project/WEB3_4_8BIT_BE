package com.backend.global.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtAuthenticationErrorCode {

	// JWT 관련 에러 코드
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, 1001, "유효하지 않은 토큰입니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 1002, "토큰이 존재하지 않습니다."),
	INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, 1003, "유효하지 않은 JWT 서명입니다."),
	REFRESH_FAIL(HttpStatus.BAD_REQUEST, 1004, "토큰 갱신에 실패했습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 1005, "토큰이 만료되었습니다."),
	UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, 1006, "지원되지 않는 JWT 토큰입니다."),
	EMPTY_CLAIMS(HttpStatus.BAD_REQUEST, 1007, "JWT 클레임이 비어 있습니다."),
	BLACK_LIST_FAIL(HttpStatus.BAD_REQUEST, 1008, "토큰을 블랙리스트 등록에 실패했습니다."),
	;

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;

}

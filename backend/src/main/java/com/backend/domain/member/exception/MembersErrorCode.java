package com.backend.domain.member.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembersErrorCode {

	PROVIDER_CONFLICT(HttpStatus.CONFLICT, 3001, "이미 다른 소셜 계정으로 가입된 이메일입니다."),
	UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, 3002, "지원하지 않는 소셜 로그인입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 3003, "해당 사용자를 찾을 수 없습니다.")
	;

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}

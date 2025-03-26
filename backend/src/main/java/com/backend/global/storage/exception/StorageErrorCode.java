package com.backend.global.storage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode {

	//공통 서버 에러 코드 500
	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, 4001, "파일 크기가 허용된 최대 크기(10MB)를 초과했습니다."),
	UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, 4002, "지원하지 않는 파일 형식입니다. (허용된 형식: PNG, JPEG, JPG)");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
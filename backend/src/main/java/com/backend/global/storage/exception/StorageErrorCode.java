package com.backend.global.storage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * StorageErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 *
 * @author vdvhk12
 */
@Getter
@RequiredArgsConstructor
public enum StorageErrorCode {

	//스토리지 에러 코드
	UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, 4001, "지원하지 않는 파일 형식입니다. (허용된 형식: PNG, JPEG, JPG)");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
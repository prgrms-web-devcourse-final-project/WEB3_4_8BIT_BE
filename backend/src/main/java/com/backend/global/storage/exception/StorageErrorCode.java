package com.backend.global.storage.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

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
public enum StorageErrorCode implements ErrorCode {

	//스토리지 에러 코드 500
	UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, 5001, "지원하지 않는 파일 형식입니다. (허용된 형식: PNG, JPEG, JPG)");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
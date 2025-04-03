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
	UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, 5001, "지원하지 않는 파일 형식입니다. (허용된 형식: PNG, JPEG, JPG)"),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, 5002, "존재하지 않는 파일 ID가 포함되어 있습니다."),
	ALREADY_UPLOADED_FILE(HttpStatus.BAD_REQUEST, 5003, "이미 업로드 완료된 파일이 포함되어 있습니다."),
	NO_FILE_ACCESS(HttpStatus.FORBIDDEN, 5004, "해당 파일에 대한 접근 권한이 없습니다."),
	S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, 5005, "S3에 파일이 존재하지 않습니다."),
	S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5006, "S3에서 파일 삭제 중 오류가 발생했습니다.")
	;

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
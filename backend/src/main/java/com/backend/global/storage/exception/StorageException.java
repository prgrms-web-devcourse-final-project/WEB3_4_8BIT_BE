package com.backend.global.storage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * StorageException
 * <p>스토리지 예외 클래스 입니다. <br><br>
 * 사용 예시: </p>
 * {@code
 * throw new StorageException(StorageErrorCode.FILE_SIZE_EXCEEDED);
 * }
 * @author vdvhk12
 */
@Getter
public class StorageException extends RuntimeException {

	private final StorageErrorCode storageErrorCode;

	/**
	 * GlobalException 생성자 입니다.
	 * @param storageErrorCode StorageErrorCode 값
	 */
	public StorageException(StorageErrorCode storageErrorCode) {
		super(storageErrorCode.getMessage());
		this.storageErrorCode = storageErrorCode;
	}

	/**
	 * 응답 HttpStatus를 반환하는 메서드 입니다.
	 * @return {@link HttpStatus}
	 */
	public HttpStatus getStatus() {
		return storageErrorCode.getHttpStatus();
	}
}

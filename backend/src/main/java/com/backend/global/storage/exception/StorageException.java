package com.backend.global.storage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
	private final StorageErrorCode storageErrorCode;

	public StorageException(StorageErrorCode storageErrorCode) {
		super(storageErrorCode.getMessage());
		this.storageErrorCode = storageErrorCode;
	}

	public HttpStatus getStatus() {
		return storageErrorCode.getHttpStatus();
	}
}

package com.backend.domain.fish.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FishException extends RuntimeException {

	private final FishErrorCode fishErrorCode;

	public FishException(final FishErrorCode fishErrorCode) {
		super(fishErrorCode.getMessage());
		this.fishErrorCode = fishErrorCode;
	}

	public HttpStatus getStatus() {

		return fishErrorCode.getHttpStatus();
	}
}
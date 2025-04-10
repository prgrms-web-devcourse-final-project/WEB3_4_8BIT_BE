package com.backend.domain.fishencyclopedia.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class FishEncyclopediaException extends GlobalException {
	private final ErrorCode errorCode;

	public FishEncyclopediaException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public FishEncyclopediaException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

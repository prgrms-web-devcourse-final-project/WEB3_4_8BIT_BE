package com.backend.domain.member.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class MemberException extends GlobalException {
	private final ErrorCode errorCode;

	public MemberException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public MemberException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

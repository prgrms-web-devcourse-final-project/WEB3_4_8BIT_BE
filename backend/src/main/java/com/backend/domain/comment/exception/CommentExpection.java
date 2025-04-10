package com.backend.domain.comment.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

@Getter
public class CommentExpection extends GlobalException {

	private final ErrorCode errorCode;

	public CommentExpection(final ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public CommentExpection(final Throwable cause, final ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

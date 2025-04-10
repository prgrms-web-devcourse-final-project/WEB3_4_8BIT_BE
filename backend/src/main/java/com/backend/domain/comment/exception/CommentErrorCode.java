package com.backend.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

	// 댓글 에러 코드 1500
	PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, 15001, "부모 댓글이 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
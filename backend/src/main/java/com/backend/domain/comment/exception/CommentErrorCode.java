package com.backend.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

	// 댓글 에러 코드 1500
	PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, 15001, "부모 댓글이 존재하지 않습니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 15002, "댓글이 존재하지 않습니다."),
	COMMENT_UNAUTHORIZED_AUTHOR(HttpStatus.FORBIDDEN, 15003, "해당 댓글 작성자가 아닙니다."),
	FISHING_TRIP_ID_NOT_VALID(HttpStatus.BAD_REQUEST, 15004, "댓글의 게시글 ID가 일치하지 않습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
package com.backend.domain.shipfishposts.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipFishPostsErrorCode {

	POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "게시글 정보가 존재하지 않음");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
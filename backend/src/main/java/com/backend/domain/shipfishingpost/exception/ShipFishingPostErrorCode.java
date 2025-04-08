package com.backend.domain.shipfishingpost.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipFishingPostErrorCode implements ErrorCode {

	POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "게시글 정보가 존재하지 않음"),
	POSTS_RESERVATION_EXIST(HttpStatus.CONFLICT, 7002, "게시글의 남은 예약이 존재하므로 삭제할 수 없습니다."),
	NOT_AUTHORITY_POSTS(HttpStatus.FORBIDDEN, 7003, "해당 게시글에 대한 권한이 없습니다."),
	POSTS_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, 7004, "요청한 인원수가 선박의 최대 수용 인원을 초과하였습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
package com.backend.domain.fishingtrippost.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishingTripPostErrorCode implements ErrorCode {

	FISHING_TRIP_POST_NOT_FOUND(HttpStatus.NOT_FOUND, 1201, "해당 동출 게시글을 찾을 수 없습니다."),
	FISHING_TRIP_POST_OVER_RECRUITMENT(HttpStatus.BAD_REQUEST, 1202, "현재 인원이 모집 인원을 초과할 수 없습니다."),
	FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR(HttpStatus.FORBIDDEN, 1203, "해당 게시글의 작성자가 아닙니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

package com.backend.domain.fishpoint.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishPointErrorCode implements ErrorCode {

	FISH_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, 1301, "해당 낚시 포인트를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

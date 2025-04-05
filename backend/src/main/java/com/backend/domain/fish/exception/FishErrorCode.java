package com.backend.domain.fish.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishErrorCode implements ErrorCode {

	FISH_NOT_FOUND(HttpStatus.NOT_FOUND, 10001, "물고기가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

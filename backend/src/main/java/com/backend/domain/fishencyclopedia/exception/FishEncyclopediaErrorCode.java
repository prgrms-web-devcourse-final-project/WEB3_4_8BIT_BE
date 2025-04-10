package com.backend.domain.fishencyclopedia.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishEncyclopediaErrorCode implements ErrorCode {;

	// 물고기 도감 에러 코드 900

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
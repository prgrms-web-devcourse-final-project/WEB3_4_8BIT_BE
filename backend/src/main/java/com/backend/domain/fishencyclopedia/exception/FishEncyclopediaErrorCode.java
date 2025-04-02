package com.backend.domain.fishencyclopedia.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishEncyclopediaErrorCode implements ErrorCode {

	// 물고기 도감 에러 코드 900
	NOT_EXISTS_FISH(HttpStatus.NOT_FOUND, 9001, "물고기가 존재하지 않습니다."),
	NOT_EXISTS_FISH_POINT(HttpStatus.NOT_FOUND, 9002, "낚시 포인트가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
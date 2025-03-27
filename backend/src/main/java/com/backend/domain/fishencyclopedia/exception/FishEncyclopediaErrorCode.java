package com.backend.domain.fishencyclopedia.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishEncyclopediaErrorCode {

	//물고기 도감 에러 코드 100
	NOT_EXISTS_FISH(HttpStatus.NOT_FOUND, 1001, "물고기가 존재하지 않습니다."),
	NOT_EXISTS_FISH_POINT(HttpStatus.NOT_FOUND, 1002, "낚시 포인트가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
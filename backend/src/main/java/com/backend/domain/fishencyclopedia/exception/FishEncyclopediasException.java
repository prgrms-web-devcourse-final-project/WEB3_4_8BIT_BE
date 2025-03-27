package com.backend.domain.fishencyclopedia.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FishEncyclopediasException extends RuntimeException {
	private final FishEncyclopediaErrorCode fishEncyclopediaErrorCode;

	public FishEncyclopediasException(FishEncyclopediaErrorCode fishEncyclopediaErrorCode) {
		super(fishEncyclopediaErrorCode.getMessage());
		this.fishEncyclopediaErrorCode = fishEncyclopediaErrorCode;
	}

	public HttpStatus getStatus() {
		return fishEncyclopediaErrorCode.getHttpStatus();
	}
}

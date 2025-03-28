package com.backend.domain.fishencyclopedia.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FishEncyclopediaException extends RuntimeException {
	private final FishEncyclopediaErrorCode fishEncyclopediaErrorCode;

	public FishEncyclopediaException(FishEncyclopediaErrorCode fishEncyclopediaErrorCode) {
		super(fishEncyclopediaErrorCode.getMessage());
		this.fishEncyclopediaErrorCode = fishEncyclopediaErrorCode;
	}

	public HttpStatus getStatus() {
		return fishEncyclopediaErrorCode.getHttpStatus();
	}
}

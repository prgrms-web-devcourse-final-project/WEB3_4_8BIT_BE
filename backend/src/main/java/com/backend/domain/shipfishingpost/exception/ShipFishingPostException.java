package com.backend.domain.shipfishingpost.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ShipFishingPostException extends RuntimeException {

	private final ShipFishingPostErrorCode shipFishingPostErrorCode;

	public ShipFishingPostException(final ShipFishingPostErrorCode shipFishingPostErrorCode) {
		super(shipFishingPostErrorCode.getMessage());
		this.shipFishingPostErrorCode = shipFishingPostErrorCode;
	}

	public HttpStatus getStatus() {

		return shipFishingPostErrorCode.getHttpStatus();
	}
}
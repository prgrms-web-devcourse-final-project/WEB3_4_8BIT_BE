package com.backend.domain.shipfishposts.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ShipFishPostsException extends RuntimeException {

	private final ShipFishPostsErrorCode shipFishPostsErrorCode;

	public ShipFishPostsException(final ShipFishPostsErrorCode shipFishPostsErrorCode) {
		super(shipFishPostsErrorCode.getMessage());
		this.shipFishPostsErrorCode = shipFishPostsErrorCode;
	}

	public HttpStatus getStatus() {

		return shipFishPostsErrorCode.getHttpStatus();
	}
}
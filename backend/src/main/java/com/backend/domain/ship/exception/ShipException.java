package com.backend.domain.ship.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ShipException extends RuntimeException {

	private final ShipErrorCode shipErrorCode;

	public ShipException(final ShipErrorCode shipErrorCode) {
		super(shipErrorCode.getMessage());
		this.shipErrorCode = shipErrorCode;
	}

	public HttpStatus getStatus() {

		return shipErrorCode.getHttpStatus();
	}
}

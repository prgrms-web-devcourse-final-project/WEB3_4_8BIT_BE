package com.backend.domain.reservation.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

	private final ReservationErrorCode reservationErrorCode;

	public ReservationException(final ReservationErrorCode reservationErrorCode) {
		super(reservationErrorCode.getMessage());
		this.reservationErrorCode = reservationErrorCode;
	}

	public HttpStatus getStatus() {

		return reservationErrorCode.getHttpStatus();
	}
}

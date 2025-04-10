package com.backend.domain.reservation.util;

import java.time.LocalDate;
import java.util.UUID;

public class ReservationUtil {

	/**
	 * 예약 넘버를 생성해주는 메서드
	 *
	 * @return {@link String}
	 */
	public static String generateReservationNumber() {
		LocalDate now = LocalDate.now();
		String UUIDNumber = UUID.randomUUID().toString().substring(0, 6);

		return String.format("%s-%s", now, UUIDNumber);
	}
}

package com.backend.domain.reservationdate.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

public class ReservationDateResponse {

	/**
	 * {
	 *     "remainCount": 3,
	 *     "isEnd": false,
	 *     "isBan": false
	 * }
	 *
	 * @param remainCount - 예약 잔여 인원
	 * @param isBan
	 */
	@Builder
	public record Detail(
		Integer remainCount,
		Boolean isBan
	) {
	}

	/**
	 * {
	 *     "availableDateList": ["2025-02-03", 2025-02-08"]
	 * }
	 *
	 * @param unAvailableDateList - 예약 가능 날짜
	 */
	@Builder
	public record UnAvailableDateList(
		List<LocalDate> unAvailableDateList
	) {
	}

}


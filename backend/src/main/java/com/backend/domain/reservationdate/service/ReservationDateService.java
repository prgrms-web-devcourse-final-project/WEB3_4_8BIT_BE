package com.backend.domain.reservationdate.service;

import java.time.LocalDate;

import com.backend.domain.reservationdate.dto.response.ReservationDateResponse;

public interface ReservationDateService {

	/**
	 * 선택한 날짜의 선상 낚시 예약 잔여 인원 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @return {@link ReservationDateResponse.Detail}
	 * @implSpec 일 단위로 예약 가능 인원을 조회한다.
	 * @author swjoon
	 */
	ReservationDateResponse.Detail getReservationDate(
		final Long shipFishingPostId,
		final LocalDate reservationDate
	);

	/**
	 * 선택한 Month 의 선상 낚시 예약 가능 일자 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @return {@link ReservationDateResponse.UnAvailableDateList}
	 * @implSpec 월 단위로 예약 가능 일자를 조회 해서 반환한다.
	 * @author swjoon
	 */
	ReservationDateResponse.UnAvailableDateList getReservationDateAvailableList(
		final Long shipFishingPostId,
		final LocalDate reservationDate
	);

	/**
	 * 예약 가능 여부를 검증하고 잔여 인원을 차감하는 메서드입니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @param guestCount {@link Long}
	 */
	void updateReservationDateWithRemainCount(
		final Long shipFishingPostId,
		final LocalDate reservationDate,
		final Integer guestCount
	);
}

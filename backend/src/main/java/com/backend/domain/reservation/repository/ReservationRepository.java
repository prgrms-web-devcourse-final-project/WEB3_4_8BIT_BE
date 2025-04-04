package com.backend.domain.reservation.repository;

import com.backend.domain.reservation.entity.Reservation;

public interface ReservationRepository {

	/**
	 * 예약 정보를 저장하는 메서드입니다.
	 *
	 * @param reservation {@link Reservation}
	 * @return {@link Reservation}
	 * @implSpec 예약 정보를 저장하고 반환합니다.
	 */
	Reservation save(final Reservation reservation);

}

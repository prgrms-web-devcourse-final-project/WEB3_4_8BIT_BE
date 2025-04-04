package com.backend.domain.reservation.repository;

import java.util.Optional;

import com.backend.domain.reservation.dto.response.ReservationResponse;
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

	/**
	 * 예약 상세정보를 조회하는 메서드입니다.
	 *
	 * @param reservationId {@link Long}
	 * @return {@link Optional<ReservationResponse.DetailWithMember>}
	 * @implSpec 예약 상세정보를 조회하고 예약자 이름을 함께 반환합니다.
	 */
	Optional<ReservationResponse.DetailWithMember> findDetailWithMemberById(final Long reservationId);

}

package com.backend.domain.reservation.converter;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.util.ReservationUtil;

public class ReservationConverter {

	/**
	 * 예약정보를 받아서 entity 로 반환하는 메서드 입니다.
	 *
	 * @param requestDto {@link ReservationRequest.Reserve}
	 * @param memberId {@link Long}
	 * @return {@link Reservation}
	 */
	public static Reservation fromReservationRequest(
		final ReservationRequest.Reserve requestDto,
		final Long memberId) {

		return Reservation.builder()
			.shipFishingPostId(requestDto.shipFishingPostId())
			.memberId(memberId)
			.reservationNumber(ReservationUtil.generateReservationNumber())
			.guestCount(requestDto.guestCount())
			.price(requestDto.price())
			.totalPrice(requestDto.totalPrice())
			.reservationDate(requestDto.reservationDate())
			.build();
	}

	/**
	 * 저장된 entity 를 dto 로 변환해서 반환하는 메서드입니다.
	 *
	 * @param reservation {@link Reservation}
	 * @return {@link ReservationResponse.Detail}
	 */
	public static ReservationResponse.Detail fromReservationResponseDetail(final Reservation reservation) {
		return ReservationResponse.Detail.builder()
			.reservationId(reservation.getReservationId())
			.memberId(reservation.getMemberId())
			.reservationNumber(reservation.getReservationNumber())
			.guestCount(reservation.getGuestCount())
			.price(reservation.getPrice())
			.totalPrice(reservation.getTotalPrice())
			.reservationDate(reservation.getReservationDate())
			.reservationStatus(reservation.getStatus())
			.build();
	}
}

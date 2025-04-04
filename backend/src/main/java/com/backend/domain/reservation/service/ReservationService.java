package com.backend.domain.reservation.service;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;

public interface ReservationService {

	/**
	 * 예약을 생성하는 메서드
	 *
	 * @param requestDto {@link ReservationRequest.Reserve}
	 * @param memberId {@link Long}
	 * @return {@link ReservationResponse.Detail}
	 * @implSpec 예약 정보와 유저 id 값을 받아 검증을 진행하고 예약 정보를 생성합니다.
	 * @author swjoon
	 */
	ReservationResponse.Detail createReservation(final ReservationRequest.Reserve requestDto, final Long memberId);

}

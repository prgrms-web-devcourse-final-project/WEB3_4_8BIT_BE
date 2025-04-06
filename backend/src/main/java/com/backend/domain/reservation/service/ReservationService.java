package com.backend.domain.reservation.service;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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

	/**
	 * 예약 내역을 상세 조회하는 메서드
	 *
	 * @param reservationId {@link Long}
	 * @param memberId {@link Long}
	 * @return {@link ReservationResponse.DetailWithMember}
	 * @implSpec 예약 Id 값을 받아 해당 예약 내역을 상세 조회합니다.
	 * @author swjoon
	 */
	ReservationResponse.DetailWithMember getReservation(final Long reservationId, final Long memberId);

	/**
	 * 유저가 예약한 내역을 조회하는 메서드
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 한 유저의 예약 내역을 조회합니다.
	 * @author swjoon
	 */
	ScrollResponse<ReservationResponse.DetailWithName> getUserReservationList(final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 선장이 예약 내역을 조회하는 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 선장의 예약신청 된 내역을 조회합니다.
	 * @author swjoon
	 */
	ScrollResponse<ReservationResponse.DetailWithName> getCaptainReservationList(final Long shipFishingPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 예약 취소 메서드
	 *
	 * @param reservationId {@link Long}
	 * @param memberId {@link Long}
	 * @implSpec 예약 Id 값을 받아 해당 예약을 취소합니다..
	 * @author swjoon
	 */
	void updateReservation(final Long reservationId, final Long memberId);
}

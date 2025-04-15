package com.backend.domain.reservation.service;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.payment.dto.request.TossPaymentRequest;

public interface ReservationService {

	/**
	 * 예약 전에 주문서를 생성하는 메서드
	 *
	 * @param requestDto 예약 정보
	 * @param memberId 유저 ID
	 * @return 주문서 정보
	 */
	ReservationResponse.Detail prepareReservation(final ReservationRequest.Reserve requestDto, final Long memberId);

	/**
	 * 결제 요청 후 재고 차감 여부로 결제 메서드
	 *
	 * @param requestDto 토스 결제 정보
	 */
	void confirmReservationPayment(final TossPaymentRequest requestDto, final Long memberId);

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
	 * 유저의 예약 내역 횟수를 조회합니다.
	 *
	 * @param memberId 유저 ID
	 * @return 유저 예약 내역 횟수
	 * @implSpec 유저의 확정된 예약 내역 횟수를 반환합니다.
	 */
	Long getReservationCount(final Long memberId);

	/**
	 * 유저가 예약한 내역을 조회하는 메서드
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 한 유저의 예약 내역을 조회합니다.
	 * @author swjoon
	 */
	ScrollResponse<ReservationResponse.DetailWithName> getUserReservationList(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 유저가 예약한 내역을 조회하는 메서드
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 한 유저의 예약 내역을 조회합니다.
	 * @author swjoon
	 */
	ScrollResponse<ReservationResponse.DetailReservationList> getUserReservationListWithImage(
		final Long memberId,
		final Boolean afterToday,
		final Boolean isConfirm,
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
		final Boolean afterToday,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 선장 마이페이지의 대시보드 내용을 조회하는 메서드
	 *
	 * @param memberId 유저 id
	 * @param limitDays 다가오는 날짜 제한
	 * @return {@link ReservationResponse.DashBoard} 다가오는 예약횟수, 오늘 예약 횟수, 작성한 게시글 수
	 * @implSpec 제한 날짜를 입력받아 오늘 예약횟수와 다가오는 예약 횟수, 작성한 게시글 수를 반환합니다.
	 */
	ReservationResponse.DashBoard getDashBoard(final Long memberId, final Integer limitDays);

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

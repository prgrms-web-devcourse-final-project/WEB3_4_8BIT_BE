package com.backend.domain.reservation.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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
	 * 예약 entity 조회 메서드
	 *
	 * @param reservationId {@link Long}
	 * @return {@link Optional<Reservation>}
	 * @implSpec 예약 상세정보를 조회하고 반환합니다.
	 */
	Optional<Reservation> findById(final Long reservationId);

	/**
	 * 유저의 예약 내역 횟수를 조회합니다.
	 *
	 * @param memberId 유저 ID
	 * @return 유저 예약 내역 횟수
	 * @implSpec 유저의 확정된 예약 내역 횟수를 반환합니다.
	 */
	Long getReservationCount(final Long memberId);

	/**
	 * 예약 상세정보를 조회하는 메서드입니다.
	 *
	 * @param reservationId {@link Long}
	 * @return {@link Optional<ReservationResponse.DetailWithMember>}
	 * @implSpec 예약 상세정보를 조회하고 예약자 이름을 함께 반환합니다.
	 */
	Optional<ReservationResponse.DetailWithMember> findDetailWithMemberById(final Long reservationId);

	/**
	 * 현재 날짜 기준으로 예약 내역을 조회하는 메서드입니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param today {@link LocalDate}
	 * @return 오늘 이후 확정된 예약 여부 true, false
	 */
	Boolean findByShipFishingPostIdAndTodayAfter(final Long shipFishingPostId, final LocalDate today);

	/**
	 * 예약 기록 조회 메서드 입니다. (일반 유저)
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 된 유저 id를 기반으로 예약 기록들을 조회하고 반환합니다.
	 */
	ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberId(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 예약 기록 조회 메서드 입니다. (일반 유저)
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailReservationList>}
	 * @implSpec 로그인 된 유저 id를 기반으로 예약 기록들을 조회하고 반환합니다.
	 */
	ScrollResponse<ReservationResponse.DetailReservationList> findDetailReservationListByMemberId(
		final Long memberId,
		final Boolean afterToday,
		final Boolean isConfirm,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 예약 기록 조회 메서드 입니다. (선장)
	 *
	 * @param memberId {@link Long}
	 * @param shipFishingPostId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 된 유저 id와 게시글 id를 기반으로 예약 기록들을 조회하고 반환합니다.
	 */
	ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberIdAndShipFishingPostId(
		final Long memberId,
		final Long shipFishingPostId,
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
	ReservationResponse.DashBoard findDashBoardByMemberId(final Long memberId, final Integer limitDays);
}

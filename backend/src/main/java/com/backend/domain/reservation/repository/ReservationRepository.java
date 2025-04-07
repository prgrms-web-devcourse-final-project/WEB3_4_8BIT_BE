package com.backend.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
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
	 * @return {@link List<Reservation>}
	 */
	List<Reservation> findByShipFishingPostIdAndTodayAfter(final Long shipFishingPostId, final LocalDate today);
	/**
	 * 예약 기록 조회 메서드 입니다. (일반 유저)
	 *
	 * @param memberId {@link Long}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ReservationResponse.DetailWithName>}
	 * @implSpec 로그인 된 유저 id를 기반으로 예약 기록들을 조회하고 반환합니다.
	 */
	ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberId(final Long memberId,
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
		final GlobalRequest.CursorRequest cursorRequestDto);
}

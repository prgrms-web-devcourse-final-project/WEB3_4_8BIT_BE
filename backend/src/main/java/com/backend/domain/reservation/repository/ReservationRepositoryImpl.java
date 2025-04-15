package com.backend.domain.reservation.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.entity.ReservationStatus;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

	private final ReservationJpaRepository reservationJpaRepository;
	private final ReservationQueryRepository reservationQueryRepository;

	@Override
	public Reservation save(final Reservation reservation) {

		return reservationJpaRepository.save(reservation);
	}

	@Override
	public Optional<Reservation> findById(final Long reservationId) {

		return reservationJpaRepository.findById(reservationId);
	}

	@Override
	public Optional<Reservation> findByReservationNumber(final String reservationNumber) {

		return reservationJpaRepository.findByReservationNumber(reservationNumber);
	}

	@Override
	public Long getReservationCount(final Long memberId) {

		return reservationJpaRepository.countByMemberIdAndStatus(memberId, ReservationStatus.CONFIRMED);
	}

	@Override
	public Optional<ReservationResponse.DetailWithMember> findDetailWithMemberById(final Long reservationId) {

		return reservationQueryRepository.findDetailWithMemberNameById(reservationId);
	}

	@Override
	public Boolean findByShipFishingPostIdAndTodayAfter(
		final Long shipFishingPostId,
		final LocalDate today) {

		return reservationQueryRepository.findReservationListByShipFishingPostIdWithReservationConfirmAfterToday(
			shipFishingPostId, today);
	}

	@Override
	public ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberId(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationQueryRepository.findDetailWithNameByMemberId(memberId, cursorRequestDto);
	}

	@Override
	public ScrollResponse<ReservationResponse.DetailReservationList> findDetailReservationListByMemberId(
		final Long memberId,
		final Boolean afterToday,
		final Boolean isConfirm,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationQueryRepository.findDetailReservationListByMemberId(
			memberId,
			afterToday,
			isConfirm,
			cursorRequestDto);
	}

	@Override
	public ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberIdAndShipFishingPostId(
		final Long memberId,
		final Long shipFishingPostId,
		final Boolean afterToday,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationQueryRepository.findDetailWithNameByMemberIdAndShipFishingPostId(
			memberId,
			shipFishingPostId,
			afterToday,
			cursorRequestDto);
	}

	@Override
	public ReservationResponse.DashBoard findDashBoardByMemberId(final Long memberId, final Integer limitDays) {

		return reservationQueryRepository.findDashBoardByMemberId(memberId, limitDays);
	}
}

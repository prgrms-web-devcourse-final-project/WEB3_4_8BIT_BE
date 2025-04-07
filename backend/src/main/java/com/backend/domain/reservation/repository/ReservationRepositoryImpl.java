package com.backend.domain.reservation.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
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
	public Optional<ReservationResponse.DetailWithMember> findDetailWithMemberById(final Long reservationId) {

		return reservationQueryRepository.findDetailWithMemberNameById(reservationId);
	}

	@Override
	public ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberId(final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationQueryRepository.findDetailWithNameByMemberId(memberId, cursorRequestDto);
	}

	@Override
	public ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberIdAndShipFishingPostId(
		final Long memberId, final Long shipFishingPostId, final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationQueryRepository.findDetailWithNameByMemberIdAndShipFishingPostId(memberId, shipFishingPostId,
			cursorRequestDto);
	}

}

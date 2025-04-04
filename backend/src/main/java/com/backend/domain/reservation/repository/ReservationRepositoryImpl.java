package com.backend.domain.reservation.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;

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
	public Optional<ReservationResponse.DetailWithMemberName> findDetailWithMemberNameById(final Long reservationId) {

		return reservationQueryRepository.findDetailWithMemberNameById(reservationId);
	}

}

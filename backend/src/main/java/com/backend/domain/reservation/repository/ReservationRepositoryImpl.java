package com.backend.domain.reservation.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.entity.Reservation;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

	private final ReservationJpaRepository reservationJpaRepository;

	@Override
	public Reservation save(final Reservation reservation) {

		return reservationJpaRepository.save(reservation);
	}
}

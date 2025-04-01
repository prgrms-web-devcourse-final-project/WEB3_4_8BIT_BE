package com.backend.domain.reservation.repository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl {

	private final ReservationJpaRepository reservationJpaRepository;

}

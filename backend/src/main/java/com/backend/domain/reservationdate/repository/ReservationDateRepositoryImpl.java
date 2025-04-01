package com.backend.domain.reservationdate.repository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationDateRepositoryImpl implements ReservationDateRepository {

	private final ReservationDateJpaRepository reservationDateJpaRepository;

}

package com.backend.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservation.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
}

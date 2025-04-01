package com.backend.domain.reservationdate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservationdate.entity.ReservationDate;

public interface ReservationDateJpaRepository extends JpaRepository<ReservationDate, Long> {
}

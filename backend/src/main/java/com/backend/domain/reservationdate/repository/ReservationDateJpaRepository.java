package com.backend.domain.reservationdate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

public interface ReservationDateJpaRepository extends JpaRepository<ReservationDate, ReservationDateId> {

}

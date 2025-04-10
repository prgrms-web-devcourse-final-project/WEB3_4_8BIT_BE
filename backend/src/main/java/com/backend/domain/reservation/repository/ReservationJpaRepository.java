package com.backend.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.entity.ReservationStatus;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

	Long countByMemberIdAndStatus(final Long memberId, final ReservationStatus status);

}

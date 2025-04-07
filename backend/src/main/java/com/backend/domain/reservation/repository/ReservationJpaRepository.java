package com.backend.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservation.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByShipFishingPostIdAndReservationDateGreaterThanEqual(final Long shipFishingPostId,
		final LocalDate today);
}

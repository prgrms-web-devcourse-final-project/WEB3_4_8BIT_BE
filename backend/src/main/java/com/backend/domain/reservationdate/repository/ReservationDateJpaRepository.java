package com.backend.domain.reservationdate.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

public interface ReservationDateJpaRepository extends JpaRepository<ReservationDate, ReservationDateId> {

	Optional<ReservationDate> findByShipFishingPostIdAndReservationDate(final Long shipFishingPostId,
		final LocalDate reservationDate);

}

package com.backend.domain.reservationdate.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

import jakarta.persistence.LockModeType;

public interface ReservationDateJpaRepository extends JpaRepository<ReservationDate, ReservationDateId> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT rd FROM ReservationDate rd WHERE rd.shipFishingPostId = :shipFishingPostId AND rd.reservationDate = :reservationDate")
	Optional<ReservationDate> findByShipFishingPostIdAndReservationDate(
		@Param("shipFishingPostId") Long shipFishingPostId,
		@Param("reservationDate") LocalDate reservationDate);
}

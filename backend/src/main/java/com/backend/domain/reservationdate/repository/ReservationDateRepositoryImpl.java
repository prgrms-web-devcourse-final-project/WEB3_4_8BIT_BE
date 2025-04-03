package com.backend.domain.reservationdate.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationDateRepositoryImpl implements ReservationDateRepository {

	private final ReservationDateJpaRepository reservationDateJpaRepository;
	private final ReservationDateQueryRepository reservationDateQueryRepository;

	@Override
	public ReservationDate save(final ReservationDate reservationDate) {

		return reservationDateJpaRepository.save(reservationDate);
	}

	@Override
	public void saveAllByBulkQuery(final List<ReservationDate> reservationDateList, final int batchSize) {

		reservationDateQueryRepository.batchInsert(reservationDateList, batchSize);
	}

	@Override
	public Optional<ReservationDate> findById(final ReservationDateId reservationDateId) {

		return reservationDateJpaRepository.findById(reservationDateId);
	}

	@Override
	public Optional<ReservationDate> findByIdWithPessimistic(
		final Long shipFishingPostId,
		final LocalDate reservationDate) {

		return reservationDateJpaRepository
			.findByShipFishingPostIdAndReservationDate(shipFishingPostId, reservationDate);
	}

	@Override
	public List<ReservationDate> findAll() {

		return reservationDateJpaRepository.findAll();
	}

	@Override
	public List<LocalDate> findUnAvailableDatesByStartDateBetweenEndDate(
		final Long shipFishingPostId,
		final LocalDate startDate,
		final LocalDate endDate) {

		return reservationDateQueryRepository
			.findUnAvailableDatesByStartDateBetweenEndDate(shipFishingPostId, startDate, endDate);
	}
}

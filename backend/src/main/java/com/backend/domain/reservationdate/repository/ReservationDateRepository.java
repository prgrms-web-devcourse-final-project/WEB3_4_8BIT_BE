package com.backend.domain.reservationdate.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

public interface ReservationDateRepository {

	/**
	 * 예약 일자 정보를 저장하는 메서드입니다.
	 *
	 * @param reservationDate {@link ReservationDate}
	 * @return {@link ReservationDate}
	 * @implSpec 예약 일자 정보를 저장합니다.
	 */
	ReservationDate save(final ReservationDate reservationDate);

	/**
	 * 여러건의 예약 일자를 쿼리 한번에 저장하는 메서드입니다.
	 *
	 * @param reservationDateList {@link List<ReservationDate>}
	 * @param batchSize {@link Integer}
	 * @implSpec 배치 사이즈를 정해서 여러건의 데이터를 한번에 저장합니다.
	 * @author swjoon
	 */
	void saveAllByBulkQuery(final List<ReservationDate> reservationDateList, final int batchSize);

	/**
	 * 복합 키로 예약 일자 정보를 반환하는 메서드입니다.
	 *
	 * @param reservationDateId {@link ReservationDateId}
	 * @return {@link Optional<ReservationDate>}
	 * @implSpec 예약 일자 정보를 반환하는 메서드입니다.
	 */
	Optional<ReservationDate> findById(final ReservationDateId reservationDateId);

	/**
	 * 비관적 락 적용하여 데이터를 조회 및 저장시 동시성 문제 해결한 메서드 입니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @return {@link Optional<ReservationDate>}
	 * @implSpec 예약내역을 비관적 락을 걸어 조회한다.
	 */
	Optional<ReservationDate> findByIdWithPessimistic(final Long shipFishingPostId, final LocalDate reservationDate);

	/**
	 * 예약 일자 데이터 전체 조회 메서드
	 *
	 * @return {@link List<ReservationDate>}
	 * @implSpec 예약 일정을 한번에 조회한다.
	 */
	List<ReservationDate> findAll();

	/**
	 * 선상 낚시 게시글의 예약 불가능한 일자 리스트를 월 단위로 조회한다.
	 *
	 * @param startDate {@link LocalDate}
	 * @param endDate {@link LocalDate}
	 * @return {@link List<LocalDate>}
	 * @implSpec 선상 낚시 게시글의 예약 불가능한 일자 리스트를 월 단위로 조회한다.
	 */
	List<LocalDate> findUnAvailableDatesByStartDateBetweenEndDate(
		final Long shipFishingPostId,
		final LocalDate startDate,
		final LocalDate endDate);
}

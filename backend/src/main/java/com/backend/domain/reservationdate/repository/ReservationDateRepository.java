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
	 * 게시글 id 와 예약 날짜가 같은 예약 일자 정보를 조회하는 메서드입니다
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @return {@link Optional<ReservationDate>}
	 * @implSpec 예약 일자 정보를 반환하는 메서드입니다.
	 */
	Optional<ReservationDate> findByShipFishingPostIdAndReservationDate(
		final Long shipFishingPostId,
		final LocalDate reservationDate);

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

	/**
	 * 예약 일자의 잔여 인원 업데이트 메서드
	 *
	 * @param shipFishingPostId 게시글 id
	 * @param updateCount 업데이트 할 인원 수
	 * @param today 오늘 날짜
	 * @implSpec 잔여 인원 증가 메서드 입니다.
	 */
	void updateRemainCountWithPlus(final Long shipFishingPostId, final Integer updateCount, final LocalDate today);

	/**
	 * 예약 일자의 잔여 인원 업데이트 메서드
	 *
	 * @param shipFishingPostId 게시글 id
	 * @param updateCount 업데이트 할 인원 수
	 * @param today 오늘 날짜
	 * @implSpec 잔여 인원 차감 메서드 입니다.
	 */
	void updateRemainCountWithMinus(final Long shipFishingPostId, final Integer updateCount, final LocalDate today);

	/**
	 * 선상낚시 게시글과 연관된 예약 일자 목록 삭제 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @implSpec 선택된 선상 낚시 게시글의 예약 일자 리스트를 전체 삭제한다.
	 */
	void deleteByShipFishingPostId(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글이 없는 예약 일자 삭제 메서드
	 *
	 * @implSpec 선상 낚시 게시글 데이터가 없는 예약 일자 리스트를 전체 삭제한다.
	 */
	void deleteOrphanReservationDate();
}

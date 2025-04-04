package com.backend.domain.reservationdate.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ReservationDateRepositoryTest extends BaseTest {

	@Autowired
	private ReservationDateRepository reservationDateRepository;

	@Test
	@DisplayName("예약 일자 리스트 저장 [BulkQuery Insert & findAll] [Repository] - Success")
	void t01() {
		// Given
		List<ReservationDate> givenResrvationList = new ArrayList<>();

		for (long i = 0; i < 100; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", i)
				.set("reservationDate", LocalDate.now().plusDays(i))
				.set("remainCount", 10)
				.sample());
		}

		for (ReservationDate reservationDate : givenResrvationList) {
			System.out.println(reservationDate.getShipFishingPostId());
		}

		// When
		long startTime = System.currentTimeMillis();
		reservationDateRepository.saveAllByBulkQuery(givenResrvationList, givenResrvationList.size());
		long elapsedTime = System.currentTimeMillis() - startTime;

		// Then
		List<ReservationDate> savedReservationDateList = reservationDateRepository.findAll();

		assertThat(savedReservationDateList).hasSize(givenResrvationList.size());

		System.out.println("Bulk insert 소요 시간: " + elapsedTime + " ms");
	}

	@Test
	@DisplayName("예약 불가능 날짜 조회 [StartDate Between EndDate] [Repository] - Success")
	void t02() {
		// Given
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());

		List<ReservationDate> givenResrvationList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", 1L)
				.set("reservationDate", startDate.plusDays(i))
				.set("remainCount", 0)
				.set("isBan", true)
				.sample());
		}

		for (int i = 10; i < 20; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", 1L)
				.set("reservationDate", startDate.plusDays(i))
				.set("remainCount", 19 - i)
				.set("isBan", false)
				.sample());
		}

		reservationDateRepository.saveAllByBulkQuery(givenResrvationList, givenResrvationList.size());

		// When
		List<LocalDate> savedList = reservationDateRepository.findUnAvailableDatesByStartDateBetweenEndDate(1L,
			startDate, endDate);

		// Then
		assertThat(savedList).hasSize(11);
	}

	@Test
	@DisplayName("예약 일자 조회 [Repository] - Success")
	void t03() {
		// Given
		ReservationDate givenReservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.now())
			.set("remainCount", 10)
			.set("isBan", false)
			.sample();

		reservationDateRepository.save(givenReservationDate);

		// When
		Optional<ReservationDate> savedReservationDate = reservationDateRepository.findByIdWithPessimistic(1L,
			LocalDate.now());

		// Then
		assertThat(savedReservationDate).isPresent();
		assertThat(savedReservationDate.get().getShipFishingPostId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("예약 일자 저장 [Repository] - Success")
	void t04() {
		// Given
		ReservationDate givenReservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.now().plusDays(1))
			.set("remainCount", 10)
			.sample();

		// When
		ReservationDate savedReservation = reservationDateRepository.save(givenReservationDate);

		// Then
		assertThat(savedReservation.getShipFishingPostId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("예약 일자 조회 [Repository] - Success")
	void t05() {
		// Given
		ReservationDate givenReservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.now().plusDays(1))
			.set("remainCount", 10)
			.sample();

		ReservationDate savedReservation = reservationDateRepository.save(givenReservationDate);

		ReservationDateId givenReservationDateId = ReservationDateConverter.fromReservationDateIdRequest(
			savedReservation.getReservationDate(), savedReservation.getShipFishingPostId());

		// When
		Optional<ReservationDate> findReservationDate = reservationDateRepository.findById(givenReservationDateId);

		//Then
		assertThat(findReservationDate).isPresent();
		assertThat(findReservationDate.get().getShipFishingPostId()).isEqualTo(1L);
	}

}

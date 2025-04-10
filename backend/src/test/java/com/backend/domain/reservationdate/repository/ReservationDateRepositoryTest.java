package com.backend.domain.reservationdate.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.chat.message.repository.MessageQueryRepository;
import com.backend.domain.chat.message.repository.MessageRepositoryImpl;
import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class),
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		MessageQueryRepository.class,
		MessageRepositoryImpl.class}
	))
public class ReservationDateRepositoryTest extends BaseTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private ReservationDateRepository reservationDateRepository;

	@AfterEach
	public void tearDown() {
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("예약 일자 리스트 저장 [BulkQuery Insert & findAll] [Repository] - Success")
	void t01() {
		// Given
		em.clear();

		List<ReservationDate> givenResrvationList = new ArrayList<>();

		for (long i = 0; i < 100; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", i)
				.set("reservationDate", LocalDate.now().plusDays(i))
				.set("remainCount", 10)
				.sample());
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

	@Test
	@DisplayName("선상 낚시 게시글 예약 일자 전체 삭제 [Repository] - Success")
	void t06() {
		// Given
		Long givenShipFishingPostId = 1L;

		List<ReservationDate> givenResrvationList = new ArrayList<>();

		for (long i = 0; i < 100; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", givenShipFishingPostId)
				.set("reservationDate", LocalDate.now().plusDays(i))
				.set("remainCount", 10)
				.sample());
		}

		reservationDateRepository.saveAllByBulkQuery(givenResrvationList, givenResrvationList.size());

		// When
		reservationDateRepository.deleteByShipFishingPostId(givenShipFishingPostId);

		// Then
		List<ReservationDate> findReservationDateList = reservationDateRepository.findAll();

		assertThat(findReservationDateList.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("선상 낚시 게시글 예약 일자 전체 삭제 [Repository] - Success")
	void t07() {
		Long givenShipFishingPostId = 1L;

		List<ReservationDate> givenResrvationList = new ArrayList<>();

		for (long i = 0; i < 100; i++) {
			givenResrvationList.add(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
				.set("shipFishingPostId", givenShipFishingPostId)
				.set("reservationDate", LocalDate.now().plusDays(i))
				.set("remainCount", 10)
				.sample());
		}

		reservationDateRepository.saveAllByBulkQuery(givenResrvationList, givenResrvationList.size());

		// When
		reservationDateRepository.deleteOrphanReservationDate();

		// Then
		List<ReservationDate> findReservationDateList = reservationDateRepository.findAll();

		assertThat(findReservationDateList.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("선상 낚시 게시글 예약 일자 업데이트 [잔여 인원 증가] [Repository] - Success")
	void t08() {
		ReservationDate givenReservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.now().plusDays(1))
			.set("remainCount", 10)
			.set("isBan", false)
			.sample();

		reservationDateRepository.save(givenReservationDate);

		reservationDateRepository.updateRemainCountWithPlus(
			givenReservationDate.getShipFishingPostId(), 7, LocalDate.now());

		em.flush();
		em.clear();

		Optional<ReservationDate> findOptionalReservationDate = reservationDateRepository
			.findByIdWithPessimistic(givenReservationDate.getShipFishingPostId(),
				givenReservationDate.getReservationDate());

		assertThat(findOptionalReservationDate.isPresent()).isTrue();
		assertThat(findOptionalReservationDate.get().getRemainCount()).isEqualTo(17);
	}

	@Test
	@DisplayName("선상 낚시 게시글 예약 일자 업데이트 [잔여 인원 감소] [Repository] - Success")
	void t09() {
		ReservationDate givenReservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.now().plusDays(1))
			.set("remainCount", 10)
			.set("isBan", false)
			.sample();

		reservationDateRepository.save(givenReservationDate);

		reservationDateRepository.updateRemainCountWithMinus(
			givenReservationDate.getShipFishingPostId(), -7, LocalDate.now());

		em.flush();
		em.clear();

		Optional<ReservationDate> findOptionalReservationDate = reservationDateRepository
			.findByIdWithPessimistic(givenReservationDate.getShipFishingPostId(),
				givenReservationDate.getReservationDate());

		assertThat(findOptionalReservationDate.isPresent()).isTrue();
		assertThat(findOptionalReservationDate.get().getRemainCount()).isEqualTo(3);
	}
}

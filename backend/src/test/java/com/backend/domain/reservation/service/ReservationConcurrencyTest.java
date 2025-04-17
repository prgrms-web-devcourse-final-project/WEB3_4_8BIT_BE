package com.backend.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReservationConcurrencyTest extends BaseTest {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private ReservationDateRepository reservationDateRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	private ShipFishingPost createShipFishingPost(int initialRemainCount) {

		return fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", null)
			.set("subject", "test")
			.set("maxGuestCount", initialRemainCount)
			.set("price", 1000L)
			.sample();
	}

	private ReservationDate createReservationDate(Long shipFishingPostId, LocalDate reservationDate,
		int initialRemainCount) {

		return fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", shipFishingPostId)
			.set("reservationDate", reservationDate)
			.set("remainCount", initialRemainCount)
			.set("isBan", false)
			.sample();
	}

	private ReservationRequest.Reserve createReservationRequest(Long shipFishingPostId, LocalDate reservationDate,
		int guestCount) {

		return ReservationRequest.Reserve.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDate)
			.guestCount(guestCount)
			.price(1000L)
			.totalPrice(1000L * guestCount)
			.build();
	}

	/**
	 * 동시 실행을 위한 헬퍼 메서드.
	 * 각 스레드에 전달할 작업을 List<Runnable> 형태로 받고, 실행 후 각 스레드에서 발생한 예외를 반환합니다.
	 */
	private List<AtomicReference<Throwable>> runConcurrentTasks(List<Runnable> tasks) throws InterruptedException {
		int threadCount = tasks.size();

		CountDownLatch latch = new CountDownLatch(1);

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		List<AtomicReference<Throwable>> exceptions = new ArrayList<>();

		for (Runnable task : tasks) {
			AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
			exceptions.add(exceptionRef);

			executorService.submit(() -> {
				try {
					latch.await();
					task.run();
				} catch (Throwable t) {
					exceptionRef.set(t);
				}
			});
		}

		latch.countDown();
		executorService.shutdown();
		executorService.awaitTermination(30, TimeUnit.SECONDS);

		return exceptions;
	}

	@BeforeAll
	static void beforeAll() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.debug("현재 JVM 타임존: {}", TimeZone.getDefault());
		log.debug("현재 시간: {}", ZonedDateTime.now());
		log.debug("현재 날짜: {}", LocalDate.now());
	}

	@Test
	@DisplayName("동일 예약일에 2명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [1명 실패] [Service] - Success")
	void t01() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(3);
		int initialRemainCount = 12;
		int guestCount1 = 7;
		int guestCount2 = 7;

		ShipFishingPost shipFishingPost = createShipFishingPost(initialRemainCount);

		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		ReservationDate reservationDate = createReservationDate(shipFishingPostId, reservationDateValue,
			initialRemainCount);

		ReservationDate reservationDateLog = reservationDateRepository.save(reservationDate);

		log.info("제공된 예약 날짜 : {}", reservationDateValue);
		log.info("db에 저장된 예약 날짜: {}", reservationDateLog.getReservationDate());

		// 두 개의 예약 요청 DTO 생성
		ReservationRequest.Reserve requestDto1 = createReservationRequest(shipFishingPostId, reservationDateValue,
			guestCount1);

		ReservationRequest.Reserve requestDto2 = createReservationRequest(shipFishingPostId, reservationDateValue,
			guestCount2);

		List<Runnable> tasks = Arrays.asList(
			() -> reservationService.createReservation(requestDto1, 1L),
			() -> reservationService.createReservation(requestDto2, 2L));

		// When
		List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);

		ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
				shipFishingPostId, reservationDateValue)
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		// Then
		assertThat(updatedReservationDate.getRemainCount()).isEqualTo(5);

		// 둘중 한 요청은 remainCount 부족으로 예외가 발생해야 함
		boolean exceptionOccurred = exceptions.stream().anyMatch(ref -> ref.get() != null);
		assertThat(exceptionOccurred).isTrue();
	}

	@Test
	@DisplayName("동일 예약일에 8명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [6명 실패] [Service] - Success")
	void t02() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(7);
		int initialRemainCount = 14;

		ShipFishingPost shipFishingPost = createShipFishingPost(initialRemainCount);
		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		ReservationDate reservationDate = createReservationDate(shipFishingPostId, reservationDateValue,
			initialRemainCount);
		reservationDateRepository.save(reservationDate);

		int threadCount = 8;

		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			tasks.add(() -> {
				ReservationRequest.Reserve requestDto = createReservationRequest(shipFishingPostId,
					reservationDateValue, 6);

				reservationService.createReservation(requestDto, ThreadLocalRandom.current().nextLong(1000));
			});
		}

		// When
		List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);

		// Then
		long errorCount = exceptions.stream().filter(ref -> ref.get() != null).count();
		assertThat(errorCount).isEqualTo(threadCount - 2);
	}

	@Test
	@DisplayName("동일 예약일에 4명이 동시 예약 취소 시 비관적 락을 통한 동시성 제어 테스트 [Service] - Success")
	void t03() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(12);
		log.debug("제공된 예약 날짜 : {}", reservationDateValue);

		int initialRemainCount = 30;

		ShipFishingPost shipFishingPost = createShipFishingPost(initialRemainCount);
		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		ReservationDate givenReservationDate = createReservationDate(shipFishingPostId, reservationDateValue,
			initialRemainCount);
		ReservationDate reservationDate = reservationDateRepository.save(givenReservationDate);

		log.debug("저장된 예약 날짜 : {}", reservationDate.getReservationDate());

		log.debug("생성 후 : {}", reservationDate.getRemainCount());

		int threadCount = 4;

		List<Long> reservationIdList = new ArrayList<>();
		List<Long> memberIdList = new ArrayList<>();

		for (long i = 1; i <= threadCount; i++) {
			ReservationRequest.Reserve requestDto = createReservationRequest(shipFishingPostId, reservationDateValue,
				6);

			ReservationResponse.Detail detail = reservationService.createReservation(requestDto, i);

			log.debug("예약 생성 : {} {}", detail.memberId(), detail.reservationId());

			memberIdList.add(detail.memberId());
			reservationIdList.add(detail.reservationId());
		}

		ReservationDate savedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
				shipFishingPostId, reservationDateValue)
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		log.debug("차감 후 : {}", savedReservationDate.getRemainCount());

		assertThat(savedReservationDate.getRemainCount()).isEqualTo(6);

		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			final int now = i;
			tasks.add(() -> {
				log.debug("예약 적용 : {} {}", reservationIdList.get(now), memberIdList.get(now));
				reservationService.updateReservation(reservationIdList.get(now), memberIdList.get(now));
			});
		}

		// When
		List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);

		// Then
		ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
				shipFishingPostId, reservationDateValue)
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		long errorCount = exceptions.stream().filter(ref -> ref.get() != null).count();

		log.debug("에러 개수 : {}", errorCount);

		assertThat(updatedReservationDate.getRemainCount()).isEqualTo(30);
	}

	@Test
	@DisplayName("동일 예약일에 4명이 동시 예약 취소 & 2명이 동시 예약 비관적 락을 통한 동시성 제어 테스트 [Service] - Success")
	void t04() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(14);
		int initialRemainCount = 36;
		int guestCount = 6;

		ShipFishingPost shipFishingPost = createShipFishingPost(initialRemainCount);
		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		ReservationDate reservationDate = createReservationDate(shipFishingPostId, reservationDateValue,
			initialRemainCount);
		reservationDateRepository.save(reservationDate);

		List<Long> reservationIdList = new ArrayList<>();
		List<Long> memberIdList = new ArrayList<>();

		int cancelCount = 4;
		int createCount = 2;

		for (long i = 1; i <= cancelCount; i++) {
			ReservationRequest.Reserve requestDto = createReservationRequest(shipFishingPostId, reservationDateValue,
				guestCount);

			ReservationResponse.Detail detail = reservationService.createReservation(requestDto, i);

			memberIdList.add(detail.memberId());
			reservationIdList.add(detail.reservationId());
		}

		ReservationDate savedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
				shipFishingPostId, reservationDateValue)
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		assertThat(savedReservationDate.getRemainCount()).isEqualTo(12);

		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < cancelCount; i++) {
			final int now = i;
			tasks.add(() -> {
				reservationService.updateReservation(reservationIdList.get(now), memberIdList.get(now));
			});
		}

		for (int i = 0; i < createCount; i++) {
			tasks.add(() -> {
				ReservationRequest.Reserve requestDto = createReservationRequest(shipFishingPostId,
					reservationDateValue, guestCount);

				reservationService.createReservation(requestDto, ThreadLocalRandom.current().nextLong(1000));
			});
		}

		List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);

		// Then
		ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
				shipFishingPostId, reservationDateValue)
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		assertThat(updatedReservationDate.getRemainCount()).isEqualTo(24);
	}

}

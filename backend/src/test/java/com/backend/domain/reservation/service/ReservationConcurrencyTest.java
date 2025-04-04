package com.backend.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReservationConcurrencyTest extends BaseTest {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private ReservationDateRepository reservationDateRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	@Test
	@DisplayName("동일 예약일에 2명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [1명 실패] [Service] - Success")
	void t01() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(1);
		int initialRemainCount = 12;
		int guestCount1 = 7;
		int guestCount2 = 7;

		// ShipFishingPost 엔티티 생성
		ShipFishingPost shipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", null)
			.set("subject", "test")
			.set("maxGuestCount", initialRemainCount)
			.set("price", 1000L)
			.sample();

		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		// ReservationDate 엔티티 생성
		ReservationDate reservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", shipFishingPostId)
			.set("reservationDate", reservationDateValue)
			.set("remainCount", initialRemainCount)
			.set("isBan", false)
			.sample();

		reservationDateRepository.save(reservationDate);

		// 두 개의 예약 요청 DTO 생성
		ReservationRequest.Reserve requestDto1 = ReservationRequest.Reserve.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDateValue)
			.guestCount(guestCount1)
			.price(1000L)
			.totalPrice(1000L * guestCount1)
			.build();

		ReservationRequest.Reserve requestDto2 = ReservationRequest.Reserve.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDateValue)
			.guestCount(guestCount2)
			.price(1000L)
			.totalPrice(1000L * guestCount2)
			.build();

		// CountDownLatch 와 ExecutorService 동시 실행 환경 구성
		int threadCount = 2;
		CountDownLatch latch = new CountDownLatch(1);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		// 각 스레드에서 발생한 예외 저장
		AtomicReference<Throwable> exceptionThread1 = new AtomicReference<>();
		AtomicReference<Throwable> exceptionThread2 = new AtomicReference<>();

		Future<?> future1 = executorService.submit(() -> {
			try {
				latch.await();
				reservationService.createReservation(requestDto1, 1L);
			} catch (Throwable t) {
				exceptionThread1.set(t);
			}
		});

		Future<?> future2 = executorService.submit(() -> {
			try {
				latch.await();
				reservationService.createReservation(requestDto2, 2L);
			} catch (Throwable t) {
				exceptionThread2.set(t);
			}
		});

		// When
		// 동시에 시작
		latch.countDown();

		future1.get();
		future2.get();

		ReservationDate updatedReservationDate = reservationDateRepository.findById(
				ReservationDateConverter.fromReservationDateIdRequest(reservationDateValue, shipFishingPostId))
			.orElseThrow(() -> new RuntimeException("ReservationDate not found"));

		// Then
		assertThat(updatedReservationDate.getRemainCount()).isEqualTo(5);

		// 둘중 한 요청은 remainCount 부족으로 예외가 발생해야 함
		boolean exceptionOccurred = exceptionThread1.get() != null || exceptionThread2.get() != null;
		assertThat(exceptionOccurred).isTrue();

		executorService.shutdown();
	}

	@Test
	@DisplayName("동일 예약일에 8명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [6명 실패] [Service] - Success")
	void t02() throws Exception {
		// Given
		LocalDate reservationDateValue = LocalDate.now().plusDays(1);
		int initialRemainCount = 14;

		ShipFishingPost shipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", null)
			.set("subject", "test")
			.set("maxGuestCount", initialRemainCount)
			.set("price", 1000L)
			.sample();

		Long shipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		ReservationDate reservationDate = fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", shipFishingPostId)
			.set("reservationDate", reservationDateValue)
			.set("remainCount", initialRemainCount)
			.set("isBan", false)
			.sample();

		reservationDateRepository.save(reservationDate);

		int threadCount = 8;
		CountDownLatch latch = new CountDownLatch(1);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		// 각 스레드에서 예외 저장
		List<AtomicReference<Throwable>> exceptions = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
			exceptions.add(exceptionRef);
			executorService.submit(() -> {
				try {
					latch.await();

					ReservationRequest.Reserve requestDto = ReservationRequest.Reserve.builder()
						.shipFishingPostId(shipFishingPostId)
						.reservationDate(reservationDateValue)
						.guestCount(6)
						.price(1000L)
						.totalPrice(6000L)
						.build();
					reservationService.createReservation(requestDto, (long)ThreadLocalRandom.current().nextInt(1000));
				} catch (Throwable t) {
					exceptionRef.set(t);
				}
			});
		}

		// When
		// 시작
		latch.countDown();

		executorService.shutdown();
		executorService.awaitTermination(30, TimeUnit.SECONDS);

		// Then
		long errorCount = exceptions.stream().filter(ref -> ref.get() != null).count();
		assertThat(errorCount).isEqualTo(threadCount - 2);
	}
}

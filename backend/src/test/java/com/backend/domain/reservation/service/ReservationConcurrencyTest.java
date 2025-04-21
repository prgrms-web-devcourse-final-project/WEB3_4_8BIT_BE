package com.backend.domain.reservation.service;

import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @SpringBootTest
public class ReservationConcurrencyTest extends BaseTest {

	// @Autowired
	// private EntityManager em;
	//
	// @Autowired
	// private ReservationService reservationService;
	//
	// @Autowired
	// private ReservationDateRepository reservationDateRepository;
	//
	// @Autowired
	// private ShipFishingPostRepository shipFishingPostRepository;
	//
	// @Autowired
	// private PlatformTransactionManager transactionManager;
	//
	// private ShipFishingPost createShipFishingPost(int initialRemainCount) {
	//
	// 	return fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
	// 		.set("shipFishingPostId", null)
	// 		.set("subject", "test")
	// 		.set("maxGuestCount", initialRemainCount)
	// 		.set("price", 1000L)
	// 		.sample();
	// }
	//
	// private ReservationDate createReservationDate(Long shipFishingPostId, LocalDate reservationDate,
	// 	int initialRemainCount) {
	//
	// 	return fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
	// 		.set("shipFishingPostId", shipFishingPostId)
	// 		.set("reservationDate", reservationDate)
	// 		.set("remainCount", initialRemainCount)
	// 		.set("isBan", false)
	// 		.sample();
	// }
	//
	// private ReservationRequest.Reserve createReservationRequest(Long shipFishingPostId, LocalDate reservationDate,
	// 	int guestCount) {
	//
	// 	return ReservationRequest.Reserve.builder()
	// 		.shipFishingPostId(shipFishingPostId)
	// 		.reservationDate(reservationDate)
	// 		.guestCount(guestCount)
	// 		.price(1000L)
	// 		.totalPrice(1000L * guestCount)
	// 		.build();
	// }
	//
	// private static final LocalDate givenDate = LocalDate.of(2040, 1, 2);
	//
	// private static Long givenShipFishingPostId;
	//
	// @BeforeAll
	// static void beforeAll() {
	// 	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	// 	log.debug("현재 JVM 타임존: {}", TimeZone.getDefault());
	// 	log.debug("현재 시간: {}", ZonedDateTime.now());
	// 	log.debug("현재 날짜: {}", LocalDate.now());
	// }
	//
	// @BeforeEach
	// void createDataBefore() {
	// 	TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
	//
	// 	int initialRemainCount = 14;
	//
	// 	ShipFishingPost givenShipFishingPost = createShipFishingPost(initialRemainCount);
	//
	// 	ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);
	//
	// 	givenShipFishingPostId = savedShipFishingPost.getShipFishingPostId();
	//
	// 	ReservationDate givenReservationDate = createReservationDate(givenShipFishingPostId, givenDate,
	// 		initialRemainCount);
	//
	// 	ReservationDate savedReservationDate = reservationDateRepository.save(givenReservationDate);
	//
	// 	log.debug("제공된 예약 일자 : {}", givenDate);
	// 	log.debug("저장된 예약 일자 : {}", savedReservationDate.getReservationDate());
	//
	// 	em.flush();
	// 	em.clear();
	//
	// 	transactionManager.commit(tx);
	// }
	//
	// @AfterEach
	// void clearTableAfter() {
	// 	TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
	// 	em.createNativeQuery("TRUNCATE TABLE reservations").executeUpdate();
	// 	em.createNativeQuery("TRUNCATE TABLE reservation_dates").executeUpdate();
	// 	em.createNativeQuery("TRUNCATE TABLE ship_fishing_posts RESTART IDENTITY").executeUpdate();
	// 	em.flush();
	// 	em.clear();
	//
	// 	transactionManager.commit(tx);
	// }
	//
	// /**
	//  * 동시 실행을 위한 헬퍼 메서드.
	//  * 각 스레드에 전달할 작업을 List<Runnable> 형태로 받고, 실행 후 각 스레드에서 발생한 예외를 반환합니다.
	//  */
	// private List<AtomicReference<Throwable>> runConcurrentTasks(List<Runnable> tasks) throws InterruptedException {
	// 	int threadCount = tasks.size();
	//
	// 	CountDownLatch latch = new CountDownLatch(1);
	//
	// 	ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
	//
	// 	List<AtomicReference<Throwable>> exceptions = new ArrayList<>();
	//
	// 	for (Runnable task : tasks) {
	// 		AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
	// 		exceptions.add(exceptionRef);
	//
	// 		executorService.submit(() -> {
	// 			try {
	// 				latch.await();
	// 				task.run();
	// 			} catch (Throwable t) {
	// 				exceptionRef.set(t);
	// 			}
	// 		});
	// 	}
	//
	// 	latch.countDown();
	// 	executorService.shutdown();
	// 	executorService.awaitTermination(30, TimeUnit.SECONDS);
	//
	// 	return exceptions;
	// }

	// @Test
	// @DisplayName("동일 예약일에 2명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [1명 실패] [Service] - Success")
	// void t01() throws Exception {
	// 	// Given
	//
	// 	int guestCount1 = 8;
	// 	int guestCount2 = 8;
	//
	// 	// 두 개의 예약 요청 DTO 생성
	// 	ReservationRequest.Reserve requestDto1 = createReservationRequest(givenShipFishingPostId, givenDate,
	// 		guestCount1);
	//
	// 	ReservationRequest.Reserve requestDto2 = createReservationRequest(givenShipFishingPostId, givenDate,
	// 		guestCount2);
	//
	// 	List<Runnable> tasks = Arrays.asList(
	// 		() -> reservationService.createReservation(requestDto1, 1L),
	// 		() -> reservationService.createReservation(requestDto2, 2L));
	//
	// 	// When
	// 	List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);
	//
	// 	ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
	// 			givenShipFishingPostId, givenDate)
	// 		.orElseThrow(() -> new RuntimeException("ReservationDate not found"));
	//
	// 	// Then
	// 	assertThat(updatedReservationDate.getRemainCount()).isEqualTo(6);
	//
	// 	// 둘중 한 요청은 remainCount 부족으로 예외가 발생해야 함
	// 	boolean exceptionOccurred = exceptions.stream().anyMatch(ref -> ref.get() != null);
	// 	assertThat(exceptionOccurred).isTrue();
	// }
	//
	// @Test
	// @DisplayName("동일 예약일에 8명이 동시 예약 요청 시 비관적 락을 통한 동시성 제어 테스트 [6명 실패] [Service] - Success")
	// void t02() throws Exception {
	// 	// Given
	// 	int threadCount = 8;
	//
	// 	List<Runnable> tasks = new ArrayList<>();
	//
	// 	for (int i = 0; i < threadCount; i++) {
	// 		tasks.add(() -> {
	// 			ReservationRequest.Reserve requestDto = createReservationRequest(givenShipFishingPostId,
	// 				givenDate, 6);
	//
	// 			reservationService.createReservation(requestDto, ThreadLocalRandom.current().nextLong(1000));
	// 		});
	// 	}
	//
	// 	// When
	// 	List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);
	//
	// 	// Then
	// 	long errorCount = exceptions.stream().filter(ref -> ref.get() != null).count();
	// 	assertThat(errorCount).isEqualTo(6);
	// }
	//
	// @Test
	// @DisplayName("동일 예약일에 4명이 동시 예약 취소 시 비관적 락을 통한 동시성 제어 테스트 [Service] - Success")
	// void t03() throws Exception {
	// 	// Given
	// 	int threadCount = 4;
	//
	// 	int guestCount = 2;
	//
	// 	List<Long> reservationIdList = new ArrayList<>();
	// 	List<Long> memberIdList = new ArrayList<>();
	//
	// 	for (long i = 1; i <= threadCount; i++) {
	// 		ReservationRequest.Reserve requestDto = createReservationRequest(givenShipFishingPostId, givenDate,
	// 			guestCount);
	//
	// 		ReservationResponse.Detail detail = reservationService.createReservation(requestDto, i);
	//
	// 		log.debug("예약 생성 : {} {}", detail.memberId(), detail.reservationId());
	//
	// 		memberIdList.add(detail.memberId());
	// 		reservationIdList.add(detail.reservationId());
	// 	}
	//
	// 	ReservationDate savedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
	// 			givenShipFishingPostId, givenDate)
	// 		.orElseThrow(() -> new RuntimeException("ReservationDate not found"));
	//
	// 	log.debug("차감 후 : {}", savedReservationDate.getRemainCount());
	//
	// 	assertThat(savedReservationDate.getRemainCount()).isEqualTo(6);
	//
	// 	List<Runnable> tasks = new ArrayList<>();
	//
	// 	for (int i = 0; i < threadCount; i++) {
	// 		final int now = i;
	// 		tasks.add(() -> {
	// 			log.debug("예약 적용 : {} {}", reservationIdList.get(now), memberIdList.get(now));
	// 			reservationService.updateReservation(reservationIdList.get(now), memberIdList.get(now));
	// 		});
	// 	}
	//
	// 	// When
	// 	List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);
	//
	// 	// Then
	// 	ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
	// 			givenShipFishingPostId, givenDate)
	// 		.orElseThrow(() -> new RuntimeException("ReservationDate not found"));
	//
	// 	long errorCount = exceptions.stream().filter(ref -> ref.get() != null).count();
	//
	// 	log.debug("에러 개수 : {}", errorCount);
	//
	// 	assertThat(updatedReservationDate.getRemainCount()).isEqualTo(14);
	// }
	//
	// @Test
	// @DisplayName("동일 예약일에 4명이 동시 예약 취소 & 2명이 동시 예약 비관적 락을 통한 동시성 제어 테스트 [Service] - Success")
	// void t04() throws Exception {
	// 	// Given
	// 	int guestCount = 2;
	//
	// 	int cancelCount = 4;
	// 	int createCount = 2;
	//
	// 	List<Long> reservationIdList = new ArrayList<>();
	// 	List<Long> memberIdList = new ArrayList<>();
	//
	// 	for (long i = 1; i <= cancelCount; i++) {
	// 		ReservationRequest.Reserve requestDto = createReservationRequest(givenShipFishingPostId, givenDate,
	// 			guestCount);
	//
	// 		ReservationResponse.Detail detail = reservationService.createReservation(requestDto, i);
	//
	// 		memberIdList.add(detail.memberId());
	// 		reservationIdList.add(detail.reservationId());
	// 	}
	//
	// 	ReservationDate savedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
	// 			givenShipFishingPostId, givenDate)
	// 		.orElseThrow(() -> new RuntimeException("ReservationDate not found"));
	//
	// 	assertThat(savedReservationDate.getRemainCount()).isEqualTo(6);
	//
	// 	List<Runnable> tasks = new ArrayList<>();
	//
	// 	for (int i = 0; i < cancelCount; i++) {
	// 		final int now = i;
	// 		tasks.add(() -> {
	// 			reservationService.updateReservation(reservationIdList.get(now), memberIdList.get(now));
	// 		});
	// 	}
	//
	// 	for (int i = 0; i < createCount; i++) {
	// 		tasks.add(() -> {
	// 			ReservationRequest.Reserve requestDto = createReservationRequest(givenShipFishingPostId, givenDate,
	// 				guestCount);
	//
	// 			reservationService.createReservation(requestDto, ThreadLocalRandom.current().nextLong(1000));
	// 		});
	// 	}
	//
	// 	List<AtomicReference<Throwable>> exceptions = runConcurrentTasks(tasks);
	//
	// 	// Then
	// 	ReservationDate updatedReservationDate = reservationDateRepository.findByShipFishingPostIdAndReservationDate(
	// 			givenShipFishingPostId, givenDate)
	// 		.orElseThrow(() -> new RuntimeException("ReservationDate not found"));
	//
	// 	assertThat(updatedReservationDate.getRemainCount()).isEqualTo(10);
	// }

}

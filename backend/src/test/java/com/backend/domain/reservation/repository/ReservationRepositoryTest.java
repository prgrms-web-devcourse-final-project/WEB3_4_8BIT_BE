package com.backend.domain.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
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

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.entity.ReservationStatus;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ReservationRepositoryTest extends BaseTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	@AfterEach
	public void tearDown() {
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("예약 정보 저장 [Repository] - Success")
	void t01() {
		// Given
		Reservation givenReservation = fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
			.set("reservationId", null)
			.set("guestCount", 1)
			.sample();

		// When
		Reservation savedReservation = reservationRepository.save(givenReservation);

		// Then
		assertThat(savedReservation.getReservationId()).isEqualTo(givenReservation.getReservationId());
	}

	@Test
	@DisplayName("예약 정보 조회 [Repository] - Success")
	void t02() {
		// Given
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test.com")
			.set("name", "member")
			.set("nickname", "nickname")
			.set("phone", "telephone")
			.sample();

		Member savedMember = memberRepository.save(givenMember);

		Reservation givenReservation = fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
			.set("reservationId", null)
			.set("memberId", savedMember.getMemberId())
			.set("guestCount", 1)
			.sample();

		Reservation savedReservation = reservationRepository.save(givenReservation);

		// When
		Optional<ReservationResponse.DetailWithMember> optionalResponseDto = reservationRepository
			.findDetailWithMemberById(savedReservation.getReservationId());

		// Then
		assertThat(optionalResponseDto.isPresent()).isTrue();

		ReservationResponse.DetailWithMember responseDto = optionalResponseDto.get();

		assertThat(responseDto.reservationId()).isEqualTo(savedReservation.getReservationId());
		assertThat(responseDto.shipFishingPostId()).isEqualTo(savedReservation.getShipFishingPostId());
		assertThat(responseDto.memberId()).isEqualTo(savedMember.getMemberId());
		assertThat(responseDto.name()).isEqualTo(savedMember.getName());
		assertThat(responseDto.reservationNumber()).isEqualTo(savedReservation.getReservationNumber());
		assertThat(responseDto.guestCount()).isEqualTo(savedReservation.getGuestCount());
		assertThat(responseDto.price()).isEqualTo(savedReservation.getPrice());
		assertThat(responseDto.totalPrice()).isEqualTo(savedReservation.getTotalPrice());
		assertThat(responseDto.reservationDate()).isEqualTo(savedReservation.getReservationDate());
		assertThat(responseDto.reservationStatus()).isEqualTo(savedReservation.getStatus());
	}

	@Test
	@DisplayName("유저별 예약 리스트 조회 [Repository] - Success")
	void t03() {
		// Given
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test.com")
			.set("name", "member")
			.set("nickname", "nickname")
			.set("phone", "telephone")
			.sample();

		Member savedMember = memberRepository.save(givenMember);

		Long memberId = savedMember.getMemberId();

		for (int i = 1; i <= 7; i++) {
			fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
				.set("reservationId", null)
				.set("memberId", memberId)
				.set("guestCount", 1)
				.set("reservationDate", LocalDate.now().plusDays(i))
				.sampleStream()
				.limit(2)
				.forEach(reservation -> {
					reservationRepository.save(reservation);
				});
		}

		GlobalRequest.CursorRequest givenCursorRequest1 = fixtureMonkeyValidation.giveMeBuilder(
				GlobalRequest.CursorRequest.class)
			.set("order", "desc")
			.set("sort", "reservationDate")
			.set("type", "next")
			.set("fieldValue", null)
			.set("id", null)
			.set("size", 6)
			.sample();

		ScrollResponse<ReservationResponse.DetailWithName> findResponseDto1 = reservationRepository.findDetailWithNameByMemberId(
			memberId, givenCursorRequest1);

		assertThat(findResponseDto1.content().get(0).reservationDate()).isEqualTo(LocalDate.now().plusDays(7));
		assertThat(findResponseDto1.pageSize()).isEqualTo(givenCursorRequest1.size());
		assertThat(findResponseDto1.numberOfElements()).isEqualTo(6);
		assertThat(findResponseDto1.isFirst()).isTrue();
		assertThat(findResponseDto1.isLast()).isFalse();

		String lastFieldValue1 = findResponseDto1.content().get(5).reservationDate().toString();
		Long lastId1 = findResponseDto1.content().get(5).reservationId();

		GlobalRequest.CursorRequest givenCursorRequest2 = fixtureMonkeyValidation.giveMeBuilder(
				GlobalRequest.CursorRequest.class)
			.set("order", "desc")
			.set("sort", "reservationDate")
			.set("type", "next")
			.set("fieldValue", lastFieldValue1)
			.set("id", lastId1)
			.set("size", 6)
			.sample();

		ScrollResponse<ReservationResponse.DetailWithName> findResponseDto2 = reservationRepository.findDetailWithNameByMemberId(
			memberId, givenCursorRequest2);

		assertThat(findResponseDto2.content().get(0).reservationDate()).isEqualTo(LocalDate.now().plusDays(4));
		assertThat(findResponseDto2.pageSize()).isEqualTo(givenCursorRequest2.size());
		assertThat(findResponseDto2.numberOfElements()).isEqualTo(6);
		assertThat(findResponseDto2.isFirst()).isFalse();
		assertThat(findResponseDto2.isLast()).isFalse();
	}

	@Test
	@DisplayName("선장 예약 리스트 조회 [선박 선택] [Repository] - Success")
	void t04() {
		// Given
		Member givenCaptain = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test.com1")
			.set("name", "member1")
			.set("nickname", "nickname1")
			.set("phone", "telephone1")
			.sample();

		Member savedCaptain = memberRepository.save(givenCaptain);

		Long captainId = savedCaptain.getMemberId();

		List<Long> savedShipFishingPostIdList = new ArrayList<>();

		for (int i = 1; i <= 2; i++) {
			ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
				.set("shipFishingPostId", null)
				.set("memberId", captainId)
				.set("subject", "subject")
				.sample();

			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

			Long savedShipFishingPostId = savedShipFishingPost.getShipFishingPostId();

			savedShipFishingPostIdList.add(savedShipFishingPostId);
		}

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test2.com")
			.set("name", "member2")
			.set("nickname", "nickname2")
			.set("phone", "telephone2")
			.sample();

		Member savedMember = memberRepository.save(givenMember);

		Long memberId = savedMember.getMemberId();

		for (Long id : savedShipFishingPostIdList) {
			for (int j = 1; j <= 7; j++) {
				fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
					.set("reservationId", null)
					.set("shipFishingPostId", id)
					.set("memberId", memberId)
					.set("guestCount", 1)
					.set("reservationDate", LocalDate.now().plusDays(j))
					.sampleStream()
					.limit(2)
					.forEach(reservation -> {
						reservationRepository.save(reservation);
					});
			}
		}
		GlobalRequest.CursorRequest givenCursorRequest1 = fixtureMonkeyValidation
			.giveMeBuilder(GlobalRequest.CursorRequest.class)
			.set("order", "desc")
			.set("sort", "reservationDate")
			.set("type", "next")
			.set("fieldValue", null)
			.set("id", null)
			.set("size", 6)
			.sample();

		ScrollResponse<ReservationResponse.DetailWithName> findResponseDto1 = reservationRepository
			.findDetailWithNameByMemberIdAndShipFishingPostId(
				captainId, savedShipFishingPostIdList.get(0), givenCursorRequest1);

		assertThat(findResponseDto1.content().get(0).reservationDate()).isEqualTo(LocalDate.now().plusDays(7));
		assertThat(findResponseDto1.content().get(0).shipFishingPostId()).isEqualTo(savedShipFishingPostIdList.get(0));
		assertThat(findResponseDto1.content().get(1).shipFishingPostId()).isEqualTo(savedShipFishingPostIdList.get(0));
		assertThat(findResponseDto1.content().get(2).shipFishingPostId()).isEqualTo(savedShipFishingPostIdList.get(0));
		assertThat(findResponseDto1.content().get(3).shipFishingPostId()).isEqualTo(savedShipFishingPostIdList.get(0));
		assertThat(findResponseDto1.pageSize()).isEqualTo(givenCursorRequest1.size());
		assertThat(findResponseDto1.numberOfElements()).isEqualTo(6);
		assertThat(findResponseDto1.isFirst()).isTrue();
		assertThat(findResponseDto1.isLast()).isFalse();
	}

	@Test
	@DisplayName("선장 예약 리스트 조회 [전체] [Repository] - Success")
	void t05() {
		// Given
		Member givenCaptain = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test.com1")
			.set("name", "member1")
			.set("nickname", "nickname1")
			.set("phone", "telephone1")
			.sample();

		Member savedCaptain = memberRepository.save(givenCaptain);

		Long captainId = savedCaptain.getMemberId();

		List<Long> savedShipFishingPostIdList = new ArrayList<>();

		for (int i = 1; i <= 2; i++) {
			ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
				.set("shipFishingPostId", null)
				.set("memberId", captainId)
				.set("subject", "subject")
				.sample();

			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

			Long savedShipFishingPostId = savedShipFishingPost.getShipFishingPostId();

			savedShipFishingPostIdList.add(savedShipFishingPostId);
		}

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test2.com")
			.set("name", "member2")
			.set("nickname", "nickname2")
			.set("phone", "telephone2")
			.sample();

		Member savedMember = memberRepository.save(givenMember);

		Long memberId = savedMember.getMemberId();

		for (Long id : savedShipFishingPostIdList) {
			for (int j = 1; j <= 7; j++) {
				fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
					.set("reservationId", null)
					.set("shipFishingPostId", id)
					.set("memberId", memberId)
					.set("guestCount", 1)
					.set("reservationDate", LocalDate.now().plusDays(j))
					.sampleStream()
					.limit(2)
					.forEach(reservation -> {
						reservationRepository.save(reservation);
					});
			}
		}

		GlobalRequest.CursorRequest givenCursorRequest1 = fixtureMonkeyValidation
			.giveMeBuilder(GlobalRequest.CursorRequest.class)
			.set("order", "desc")
			.set("sort", "reservationDate")
			.set("type", "next")
			.set("fieldValue", null)
			.set("id", null)
			.set("size", 6)
			.sample();

		ScrollResponse<ReservationResponse.DetailWithName> findResponseDto1 = reservationRepository
			.findDetailWithNameByMemberIdAndShipFishingPostId(
				captainId, null, givenCursorRequest1);

		log.debug(" {} ", findResponseDto1.content().toString());

		assertThat(findResponseDto1.content().get(0).reservationDate()).isEqualTo(LocalDate.now().plusDays(7));
		assertThat(findResponseDto1.pageSize()).isEqualTo(givenCursorRequest1.size());
		assertThat(findResponseDto1.numberOfElements()).isEqualTo(6);
		assertThat(findResponseDto1.isFirst()).isTrue();
		assertThat(findResponseDto1.isLast()).isFalse();
	}

	@Test
	@DisplayName("오늘자 이후 예약 정보 조회 [Repository] - Success")
	void t06() {
		// Given
		Long givenShipFishingPostId = 1L;

		fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
			.set("reservationId", null)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("reservationDate", LocalDate.now())
			.set("guestCount", 1)
			.set("status", ReservationStatus.CANCELLED)
			.sampleStream()
			.limit(1)
			.forEach(reservation ->
				reservationRepository.save(reservation));

		fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
			.set("reservationId", null)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("reservationDate", LocalDate.now().plusDays(10))
			.set("guestCount", 1)
			.set("status", ReservationStatus.CANCELLED)
			.sampleStream()
			.limit(5)
			.forEach(reservation ->
				reservationRepository.save(reservation));

		fixtureMonkeyBuilder.giveMeBuilder(Reservation.class)
			.set("reservationId", null)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("reservationDate", LocalDate.now().minusDays(10))
			.set("guestCount", 1)
			.set("status", ReservationStatus.CANCELLED)
			.sampleStream()
			.limit(5)
			.forEach(reservation ->
				reservationRepository.save(reservation));

		// When
		Boolean isExists = reservationRepository.findByShipFishingPostIdAndTodayAfter(givenShipFishingPostId,
			LocalDate.now());

		assertThat(isExists).isFalse();
	}
}
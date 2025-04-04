package com.backend.domain.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ReservationRepositoryTest extends BaseTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReservationRepository reservationRepository;

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
		Optional<ReservationResponse.DetailWithMemberName> optionalResponseDto = reservationRepository
			.findDetailWithMemberNameById(savedReservation.getReservationId());

		// Then
		assertThat(optionalResponseDto.isPresent()).isTrue();

		ReservationResponse.DetailWithMemberName responseDto = optionalResponseDto.get();

		assertThat(responseDto.reservationId()).isEqualTo(savedReservation.getReservationId());
		assertThat(responseDto.name()).isEqualTo(savedMember.getName());
		assertThat(responseDto.reservationNumber()).isEqualTo(savedReservation.getReservationNumber());
		assertThat(responseDto.guestCount()).isEqualTo(savedReservation.getGuestCount());
		assertThat(responseDto.price()).isEqualTo(savedReservation.getPrice());
		assertThat(responseDto.totalPrice()).isEqualTo(savedReservation.getTotalPrice());
		assertThat(responseDto.reservationDate()).isEqualTo(savedReservation.getReservationDate());
		assertThat(responseDto.reservationStatus()).isEqualTo(savedReservation.getStatus());
	}
}

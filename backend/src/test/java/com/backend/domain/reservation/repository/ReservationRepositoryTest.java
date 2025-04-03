package com.backend.domain.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.entity.Reservation;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ReservationRepositoryTest extends BaseTest {

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

}

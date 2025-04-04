package com.backend.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.exception.ReservationErrorCode;
import com.backend.domain.reservation.exception.ReservationException;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest extends BaseTest {

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private ReservationServiceImpl reservationServiceImpl;

	@Test
	@DisplayName("예약 정보 조회 [Service] - Success")
	void t01() {
		// Given
		ReservationResponse.DetailWithMemberName givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMemberName.class).set("reservationId", 1L).sample();

		// When
		when(reservationRepository.findDetailWithMemberNameById(any(Long.class))).thenReturn(
			Optional.ofNullable(givenResponseDto));

		// Then
		ReservationResponse.DetailWithMemberName savedResponseDto = reservationServiceImpl.getReservation(
			givenResponseDto.reservationId());

		assertThat(savedResponseDto.reservationId()).isEqualTo(givenResponseDto.reservationId());
	}

	@Test
	@DisplayName("예약 정보 조회 [Service] - Fail")
	void t02() {
		// Given
		ReservationResponse.DetailWithMemberName givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMemberName.class).set("reservationId", 1L).sample();

		// When
		when(reservationRepository.findDetailWithMemberNameById(any(Long.class))).thenReturn(Optional.empty());

		// Then

		assertThatThrownBy(() -> reservationServiceImpl.getReservation(givenResponseDto.reservationId())).isInstanceOf(
			ReservationException.class).hasMessageContaining(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage());
	}

}

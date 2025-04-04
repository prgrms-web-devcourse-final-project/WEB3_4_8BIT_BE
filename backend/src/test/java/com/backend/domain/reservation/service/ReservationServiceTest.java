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
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest extends BaseTest {

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@InjectMocks
	private ReservationServiceImpl reservationServiceImpl;

	@Test
	@DisplayName("예약 정보 조회 [예약자 본인] [Service] - Success")
	void t01() {
		// Given
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation
			.giveMeBuilder(ReservationResponse.DetailWithMember.class)
			.set("reservationId", 1L)
			.set("memberId", 1L)
			.sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenShipFishingPost));

		// Then
		ReservationResponse.DetailWithMember savedResponseDto = reservationServiceImpl.getReservation(
			givenResponseDto.reservationId(), 1L);

		assertThat(savedResponseDto.reservationId()).isEqualTo(givenResponseDto.reservationId());
	}

	@Test
	@DisplayName("예약 정보 조회 [해당 예약 선장] [Service] - Success")
	void t02() {
		// Given
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation
			.giveMeBuilder(ReservationResponse.DetailWithMember.class)
			.set("reservationId", 1L)
			.set("memberId", 1L)
			.sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenShipFishingPost));

		// Then
		ReservationResponse.DetailWithMember savedResponseDto = reservationServiceImpl.getReservation(
			givenResponseDto.reservationId(), 2L);

		assertThat(savedResponseDto.reservationId()).isEqualTo(givenResponseDto.reservationId());
	}

	@Test
	@DisplayName("예약 정보 조회 [게시글 존재 x] [Service] - Fail")
	void t03() {
		// Given
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMember.class).set("reservationId", 1L).sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class))).thenReturn(Optional.empty());

		// Then

		assertThatThrownBy(
			() -> reservationServiceImpl.getReservation(givenResponseDto.reservationId(), 1L)).isInstanceOf(
			ReservationException.class).hasMessageContaining(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("예약 정보 조회 [예약자 or 선장이 아님] [Service] - Fail")
	void t04() {
		// Given
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation
			.giveMeBuilder(ReservationResponse.DetailWithMember.class)
			.set("reservationId", 1L)
			.set("memberId", 1L)
			.sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenShipFishingPost));

		assertThatThrownBy(
			() -> reservationServiceImpl.getReservation(givenResponseDto.reservationId(), 3L)).isInstanceOf(
				ReservationException.class)
			.hasMessageContaining(ReservationErrorCode.NOT_AUTHORITY_RESERVATION.getMessage());
	}

}

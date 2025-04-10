package com.backend.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
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
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
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
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMember.class).set("reservationId", 1L).set("memberId", 1L).sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class))).thenReturn(
			Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShipFishingPost));

		// Then
		ReservationResponse.DetailWithMember savedResponseDto = reservationServiceImpl.getReservation(
			givenResponseDto.reservationId(), 1L);

		assertThat(savedResponseDto.reservationId()).isEqualTo(givenResponseDto.reservationId());
	}

	@Test
	@DisplayName("예약 정보 조회 [해당 예약 선장] [Service] - Success")
	void t02() {
		// Given
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMember.class).set("reservationId", 1L).set("memberId", 1L).sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class))).thenReturn(
			Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShipFishingPost));

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
		ReservationResponse.DetailWithMember givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ReservationResponse.DetailWithMember.class).set("reservationId", 1L).set("memberId", 1L).sample();

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("memberId", 2L)
			.sample();

		// When
		when(reservationRepository.findDetailWithMemberById(any(Long.class))).thenReturn(
			Optional.ofNullable(givenResponseDto));

		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShipFishingPost));

		assertThatThrownBy(
			() -> reservationServiceImpl.getReservation(givenResponseDto.reservationId(), 3L)).isInstanceOf(
				ReservationException.class)
			.hasMessageContaining(ReservationErrorCode.NOT_AUTHORITY_RESERVATION.getMessage());
	}

	@Test
	@DisplayName("예약 목록 조회 [일반 유저] [Service] - Success")
	void t05() {
		// Given
		GlobalRequest.CursorRequest givenCursorRequestDto = fixtureMonkeyRecord.giveMeBuilder(
			GlobalRequest.CursorRequest.class).set("size", 6).sample();

		List<ReservationResponse.DetailWithName> givenDetailList = fixtureMonkeyRecord.giveMeBuilder(
			ReservationResponse.DetailWithName.class).sampleList(6);

		boolean givenHasNext = false;

		ScrollResponse<ReservationResponse.DetailWithName> givenScrollResponse = ScrollResponse.from(givenDetailList,
			givenCursorRequestDto.size(), givenDetailList.size(), true, givenHasNext);

		// When
		when(reservationRepository.findDetailWithNameByMemberId(any(Long.class), eq(givenCursorRequestDto))).thenReturn(
			givenScrollResponse);

		// Then
		ScrollResponse<ReservationResponse.DetailWithName> findScrollResponse = reservationServiceImpl.getUserReservationList(
			1L, givenCursorRequestDto);

		assertThat(givenScrollResponse.content().size()).isEqualTo(findScrollResponse.content().size());
	}

	@Test
	@DisplayName("예약 목록 조회 [선장] [Service] - Success")
	void t06() {
		// Given
		GlobalRequest.CursorRequest givenCursorRequestDto = fixtureMonkeyRecord.giveMeBuilder(
			GlobalRequest.CursorRequest.class).set("size", 6).sample();

		List<ReservationResponse.DetailWithName> givenDetailList = fixtureMonkeyRecord.giveMeBuilder(
			ReservationResponse.DetailWithName.class).sampleList(6);

		boolean givenHasNext = false;

		ScrollResponse<ReservationResponse.DetailWithName> givenScrollResponse = ScrollResponse.from(givenDetailList,
			givenCursorRequestDto.size(), givenDetailList.size(), true, givenHasNext);

		// When
		when(reservationRepository.findDetailWithNameByMemberIdAndShipFishingPostId(any(Long.class), any(Long.class),
			eq(true),
			eq(givenCursorRequestDto))).thenReturn(givenScrollResponse);

		// Then
		ScrollResponse<ReservationResponse.DetailWithName> findScrollResponse = reservationServiceImpl
			.getCaptainReservationList(1L, 1L, true, givenCursorRequestDto);

		assertThat(givenScrollResponse.content().size()).isEqualTo(findScrollResponse.content().size());
	}
}

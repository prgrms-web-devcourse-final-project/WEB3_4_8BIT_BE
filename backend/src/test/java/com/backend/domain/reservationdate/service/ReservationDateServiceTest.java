package com.backend.domain.reservationdate.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.reservationdate.dto.response.ReservationDateResponse;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ReservationDateServiceTest extends BaseTest {

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@Mock
	private ReservationDateRepository reservationDateRepository;

	@InjectMocks
	private ReservationDateServiceImpl reservationDateServiceImpl;

	@Test
	@DisplayName("예약 일자 조회 [기존에 존재하는 정보] [Service] - Success")
	void t01() {
		// Given
		ShipFishingPost savedShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", 1L)
			.set("subject", "test")
			.set("content", "test")
			.set("maxGuestCount", 1)
			.sample();

		ReservationDate givenReservationDate = fixtureMonkeyBuilder
			.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.of(2025, 4, 2))
			.set("remainCount", 1)
			.set("isBan", false)
			.sample();

		ReservationDateResponse.Detail givenResponseDto = fixtureMonkeyBuilder
			.giveMeBuilder(ReservationDateResponse.Detail.class)
			.set("remainCount", 1)
			.set("isBan", false)
			.sample();

		// When
		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(
			Optional.ofNullable(savedShipFishingPost));
		when(reservationDateRepository.findById(any(ReservationDateId.class))).thenReturn(
			Optional.ofNullable(givenReservationDate));

		ReservationDateResponse.Detail savedResponseDto =
			reservationDateServiceImpl.getReservationDate(1L, LocalDate.of(2025, 4, 2));

		// Then
		assertThat(savedResponseDto.remainCount()).isEqualTo(givenResponseDto.remainCount());
		assertThat(savedResponseDto.isBan()).isEqualTo(givenResponseDto.isBan());
	}

	@Test
	@DisplayName("예약 일자 조회 [기존에 없던 정보] [Service] - Success")
	void t02() {
		// Given
		ShipFishingPost savedShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", 1L)
			.set("subject", "test")
			.set("content", "test")
			.set("maxGuestCount", 1)
			.sample();

		ReservationDate givenReservationDate = fixtureMonkeyBuilder
			.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", 1L)
			.set("reservationDate", LocalDate.of(2025, 4, 2))
			.set("remainCount", 1)
			.set("isBan", false)
			.sample();

		ReservationDateResponse.Detail givenResponseDto = fixtureMonkeyBuilder
			.giveMeBuilder(ReservationDateResponse.Detail.class)
			.set("remainCount", 1)
			.set("isBan", false)
			.sample();

		// When
		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(
			Optional.ofNullable(savedShipFishingPost));
		when(reservationDateRepository.findById(any(ReservationDateId.class))).thenReturn(
			Optional.empty());
		when(reservationDateRepository.save(any(ReservationDate.class)))
			.thenReturn(givenReservationDate);

		ReservationDateResponse.Detail savedResponseDto =
			reservationDateServiceImpl.getReservationDate(1L, LocalDate.of(2025, 4, 2));

		// Then
		assertThat(savedResponseDto.remainCount()).isEqualTo(givenResponseDto.remainCount());
		assertThat(savedResponseDto.isBan()).isEqualTo(givenResponseDto.isBan());
	}

	@Test
	@DisplayName("예약 일자 조회 [shipFishingPostException] [Service] - Fail")
	void t03() {
		// Given

		// When
		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(
			Optional.empty());

		// Then
		assertThatThrownBy(
			() -> reservationDateServiceImpl.getReservationDate(1L, LocalDate.of(2025, 4, 2)))
			.isInstanceOf(ShipFishingPostException.class)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("예약 불가 일자 리스트 조회 [Service] - Success")
	void t04() {
		// Given
		Long givenShipFishingPostId = 1L;

		LocalDate givenReservationDate = LocalDate.of(2025, 4, 2);

		List<LocalDate> givenUnAvailableDateList = List.of(
			givenReservationDate.plusDays(1),
			givenReservationDate.plusDays(3)
		);

		when(reservationDateRepository.findUnAvailableDatesByStartDateBetweenEndDate(
			eq(givenShipFishingPostId),
			any(LocalDate.class),
			any(LocalDate.class)
		)).thenReturn(givenUnAvailableDateList);

		// When
		ReservationDateResponse.UnAvailableDateList savedUnAvailableDateList =
			reservationDateServiceImpl.getReservationDateAvailableList(givenShipFishingPostId, givenReservationDate);

		// Then
		assertThat(savedUnAvailableDateList.unAvailableDateList().size()).isEqualTo(givenUnAvailableDateList.size());
	}

}

package com.backend.domain.shipfishposts.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.reservationdate.service.ReservationDateService;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.converter.ShipFishingPostConverter;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpost.service.ShipFishingPostServiceImpl;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.S3StorageService;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ShipFishingPostServiceTest extends BaseTest {

	@Mock
	private ShipRepository shipRepository;

	@Mock
	private FishRepository fishRepository;

	@Mock
	private StorageRepository storageRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@Mock
	private ReservationDateRepository reservationDateRepository;

	@Mock
	private S3StorageService s3StorageService;

	@Mock
	private ReservationDateService reservationDateService;

	@InjectMocks
	private ShipFishingPostServiceImpl shipFishingPostServiceImpl;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				ShipFishingPostRequest.Create.class)
			.set("shipId", 1L)
			.set("fishIdList", List.of(1L))
			.set("maxGuestCount", 5)
			.sample();

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", 1L)
			.set("memberId", 1L)
			.set("passengerCapacity", 10)
			.sample();

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishingPostRequestCreate(
			givenRequestDto, 1L);

		ShipFishingPost savedShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", 1L)
			.set("subject", givenShipFishingPost.getSubject())
			.set("content", givenShipFishingPost.getContent())
			.sample();

		// When
		when(shipRepository.findById(1L)).thenReturn(Optional.of(givenShip));
		when(fishRepository.findAllById(givenRequestDto.fishIdList())).thenReturn(
			List.of(Fish.builder().fishId(1L).build()));
		when(shipFishingPostRepository.save(any(ShipFishingPost.class))).thenReturn(savedShipFishingPost);

		Long savedId = shipFishingPostServiceImpl.createShipFishingPost(givenRequestDto, 1L);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [SHIP_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeOne(
			ShipFishingPostRequest.Create.class);

		ShipFishingPostConverter.fromShipFishingPostRequestCreate(givenRequestDto, 1L);

		// When

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.createShipFishingPost(givenRequestDto, 1L)).isInstanceOf(
				ShipException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipErrorCode.SHIP_NOT_FOUND)
			.hasMessageContaining(ShipErrorCode.SHIP_NOT_FOUND.getMessage());

	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [SHIP_MISMATCH_MEMBER_ID] [Service] - Fail")
	void t03() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
			ShipFishingPostRequest.Create.class).set("shipId", 1L).sample();

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class).set("shipId", 1L).set("memberId", 2L).sample();

		ShipFishingPostConverter.fromShipFishingPostRequestCreate(givenRequestDto, 1L);

		// When
		when(shipRepository.findById(1L)).thenReturn(Optional.of(givenShip));

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.createShipFishingPost(givenRequestDto, 1L)).isInstanceOf(
				ShipException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipErrorCode.SHIP_MISMATCH_MEMBER_ID)
			.hasMessageContaining(ShipErrorCode.SHIP_MISMATCH_MEMBER_ID.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [POSTS_CAPACITY_EXCEEDED] [Service] - Fail")
	void t04() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				ShipFishingPostRequest.Create.class)
			.set("shipId", 1L)
			.set("fishIdList", List.of(1L))
			.set("maxGuestCount", 15)
			.sample();

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", 1L)
			.set("memberId", 1L)
			.set("passengerCapacity", 10)
			.sample();

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishingPostRequestCreate(
			givenRequestDto, 1L);

		fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", 1L)
			.set("subject", givenShipFishingPost.getSubject())
			.set("content", givenShipFishingPost.getContent())
			.sample();

		// When
		when(shipRepository.findById(1L)).thenReturn(Optional.of(givenShip));

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.createShipFishingPost(givenRequestDto, 1L)).isInstanceOf(
				ShipFishingPostException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipFishingPostErrorCode.POSTS_CAPACITY_EXCEEDED)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_CAPACITY_EXCEEDED.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [Service] - Success")
	void t05() {
		// given
		Long shipFishingPostId = 1L;
		List<Long> fileIdList = List.of(101L, 102L);
		List<Long> fishIdList = List.of(201L, 202L);

		ShipFishingPostResponse.Detail givenDetail = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPostResponse.Detail.class)
			.set("shipFishingPostId", shipFishingPostId)
			.set("fileIdList", fileIdList)
			.set("fishIdList", fishIdList)
			.sample();

		ShipFishingPostResponse.DetailAll givenDetailAll = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPostResponse.DetailAll.class)
			.set("detailShipFishingPost", givenDetail)
			.sample();

		List<File> givenFileList = new ArrayList<>();

		for (int i = 1; i <= 2; i++) {
			givenFileList.add(fixtureMonkeyBuilder
				.giveMeBuilder(File.class)
				.set("fileId", 100L + i)
				.set("url", String.format("http://url%d.com", i))
				.sample());
		}

		List<Fish> givenFishList = new ArrayList<>();

		for (int i = 1; i <= 2; i++) {
			givenFishList.add(fixtureMonkeyBuilder
				.giveMeBuilder(Fish.class)
				.set("fishId", 200L + i)
				.set("name", String.format("test%d", i))
				.sample());
		}

		when(shipFishingPostRepository.findDetailAllById(shipFishingPostId)).thenReturn(Optional.of(givenDetailAll));
		when(storageRepository.findAllById(fileIdList)).thenReturn(givenFileList);
		when(fishRepository.findAllById(fishIdList)).thenReturn(givenFishList);

		// when
		ShipFishingPostResponse.DetailWithFileUrlAndFishName responseDto =
			shipFishingPostServiceImpl.getShipFishingPostAll(shipFishingPostId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.fileUrlList()).isEqualTo(List.of("http://url1.com", "http://url2.com"));
		assertThat(responseDto.fishNameList()).isEqualTo(List.of("test1", "test2"));
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [POSTS_NOT_FOUND] [Service] - Fail")
	void t06() {
		// Given

		// When
		when(shipFishingPostRepository.findDetailAllById(1L)).thenReturn(Optional.empty());

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.getShipFishingPostAll(1L))
			.isInstanceOf(ShipFishingPostException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 삭제 [Service] - Success")
	void t07() {
		// Given
		Long givenShipFishingPostId = 1L;
		Long givenMemberId = 1L;

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("subject", "subject")
			.set("content", "content")
			.set("memberId", givenMemberId)
			.sample();

		// When
		when(shipFishingPostRepository.findById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenShipFishingPost));
		when(reservationRepository.findByShipFishingPostIdAndTodayAfter(any(Long.class), any(LocalDate.class)))
			.thenReturn(List.of());
		doNothing().when(s3StorageService).deleteFilesByIdList(any(Long.class), any(List.class));

		// Then
		shipFishingPostServiceImpl.deleteShipFishingPost(givenShipFishingPostId, givenMemberId);
	}

	@Test
	@DisplayName("선상 낚시 게시글 삭제 [게시글 없음] [Service] - Fail")
	void t08() {
		// Given
		Long givenShipFishingPostId = 1L;
		Long givenMemberId = 1L;

		fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("subject", "subject")
			.set("content", "content")
			.set("memberId", givenMemberId)
			.sample();

		// When
		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		// Then
		assertThatThrownBy(
			() -> shipFishingPostServiceImpl.deleteShipFishingPost(givenShipFishingPostId, givenMemberId))
			.isInstanceOf(ShipFishingPostException.class)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 삭제 [게시글 권한 없음] [Service] - Fail")
	void t09() {
		// Given
		Long givenShipFishingPostId = 1L;
		Long givenMemberId = 1L;
		Long wrongMemberId = 2L;

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("subject", "subject")
			.set("content", "content")
			.set("memberId", wrongMemberId)
			.sample();

		// When
		when(shipFishingPostRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShipFishingPost));

		// Then
		assertThatThrownBy(
			() -> shipFishingPostServiceImpl.deleteShipFishingPost(givenShipFishingPostId, givenMemberId))
			.isInstanceOf(ShipFishingPostException.class)
			.hasMessageContaining(ShipFishingPostErrorCode.NOT_AUTHORITY_POSTS.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 삭제 [잔여 예약 존재] [Service] - Fail")
	void t10() {
		// Given
		Long givenShipFishingPostId = 1L;
		Long givenMemberId = 1L;

		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", givenShipFishingPostId)
			.set("subject", "subject")
			.set("content", "content")
			.set("memberId", givenMemberId)
			.sample();

		List<Reservation> givenRemainReservation = fixtureMonkeyBuilder.giveMeBuilder(Reservation.class).sampleList(3);

		// When
		when(shipFishingPostRepository.findById(any(Long.class)))
			.thenReturn(Optional.ofNullable(givenShipFishingPost));
		when(reservationRepository.findByShipFishingPostIdAndTodayAfter(any(Long.class), any(LocalDate.class)))
			.thenReturn(givenRemainReservation);

		// Then
		assertThatThrownBy(
			() -> shipFishingPostServiceImpl.deleteShipFishingPost(givenShipFishingPostId, givenMemberId))
			.isInstanceOf(ShipFishingPostException.class)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_RESERVATION_EXIST.getMessage());
	}
}

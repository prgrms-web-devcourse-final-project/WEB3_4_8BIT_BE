package com.backend.domain.shipfishposts.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.backend.global.Util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ShipFishingPostServiceTest extends BaseTest {

	@Mock
	private ShipRepository shipRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@InjectMocks
	private ShipFishingPostServiceImpl shipFishingPostServiceImpl;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeOne(
			ShipFishingPostRequest.Create.class);

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("memberId", 1L).sample();

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			1L);

		ReflectionTestUtils.setField(givenShipFishingPost, "shipFishingPostId", 1L);

		// When
		when(shipRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShip));
		when(shipFishingPostRepository.save(any(ShipFishingPost.class))).thenReturn(givenShipFishingPost);

		Long savedId = shipFishingPostServiceImpl.saveShipFishingPost(givenRequestDto, 1L);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [SHIP_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeOne(
			ShipFishingPostRequest.Create.class);

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			1L);

		// When

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.saveShipFishingPost(givenRequestDto, 1L))
			.isInstanceOf(ShipException.class)
			.hasFieldOrPropertyWithValue("shipErrorCode", ShipErrorCode.SHIP_NOT_FOUND)
			.hasMessageContaining(ShipErrorCode.SHIP_NOT_FOUND.getMessage());

	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [SHIP_MISMATCH_MEMBER_ID] [Service] - Fail")
	void t03() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeOne(
			ShipFishingPostRequest.Create.class);

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("memberId", 2L).sample();

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			1L);

		// When
		when(shipRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(givenShip));

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.saveShipFishingPost(givenRequestDto, 1L))
			.isInstanceOf(ShipException.class)
			.hasFieldOrPropertyWithValue("shipErrorCode", ShipErrorCode.SHIP_MISMATCH_MEMBER_ID)
			.hasMessageContaining(ShipErrorCode.SHIP_MISMATCH_MEMBER_ID.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.Detail] [Service] - Success")
	void t04() {
		// Given
		ShipFishingPostResponse.Detail givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(
			ShipFishingPostResponse.Detail.class).set("shipFishingPostId", 1L).sample();

		// When
		when(shipFishingPostRepository.findDetailById(1L)).thenReturn(Optional.of(givenResponseDto));

		ShipFishingPostResponse.Detail savedResponseDto = shipFishingPostServiceImpl.getShipFishingPost(1L);

		// Then
		verify(shipFishingPostRepository, times(1)).findDetailById(1L);
		assertThat(savedResponseDto).isEqualTo(givenResponseDto);
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [POSTS_NOT_FOUND] [Service] - Fail")
	void t05() {
		// Given

		// When
		when(shipFishingPostRepository.findDetailById(1L)).thenReturn(Optional.empty());

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.getShipFishingPost(1L))
			.isInstanceOf(ShipFishingPostException.class)
			.hasFieldOrPropertyWithValue("shipFishingPostErrorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [Service] - Success")
	void t06() {
		// Given
		ShipFishingPostResponse.DetailAll givenDetailAll = fixtureMonkeyValidation.giveMeBuilder(
			ShipFishingPostResponse.DetailAll.class).sample();

		// When
		when(shipFishingPostRepository.findDetailAllById(1L)).thenReturn(Optional.of(givenDetailAll));

		ShipFishingPostResponse.DetailAll savedDetailAll = shipFishingPostServiceImpl.getShipFishingPostAll(1L);

		// Then
		assertThat(savedDetailAll).isEqualTo(givenDetailAll);
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [POSTS_NOT_FOUND] [Service] - Fail")
	void t07() {
		// Given

		// When
		when(shipFishingPostRepository.findDetailAllById(1L)).thenReturn(Optional.empty());

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.getShipFishingPostAll(1L))
			.isInstanceOf(ShipFishingPostException.class)
			.hasFieldOrPropertyWithValue("shipFishingPostErrorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}
}

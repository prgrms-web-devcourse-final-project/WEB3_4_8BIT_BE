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

	@InjectMocks
	private ShipFishingPostServiceImpl shipFishingPostServiceImpl;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeOne(
			ShipFishingPostRequest.Create.class);

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto);

		ReflectionTestUtils.setField(givenShipFishingPost, "shipFishingPostId", 1L);

		// When
		when(shipFishingPostRepository.save(any(ShipFishingPost.class))).thenReturn(givenShipFishingPost);

		Long savedId = shipFishingPostServiceImpl.save(givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [Service] - Success")
	void t02() {
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
	@DisplayName("선상 낚시 게시글 상세 조회 [Post Not Exists] [Service] - Fail")
	void t03() {
		// Given

		// When
		when(shipFishingPostRepository.findDetailById(1L)).thenReturn(Optional.empty());

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.getShipFishingPost(1L))
			.isInstanceOf(ShipFishingPostException.class)
			.hasFieldOrPropertyWithValue("shipFishingPostErrorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}
}

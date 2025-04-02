package com.backend.domain.shipfishposts.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
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
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ShipFishingPostServiceTest extends BaseTest {

	@Mock
	private ShipRepository shipRepository;

	@Mock
	private FishRepository fishRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@InjectMocks
	private ShipFishingPostServiceImpl shipFishingPostServiceImpl;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				ShipFishingPostRequest.Create.class)
			.set("shipId", 1L)
			.set("fishList", List.of(1L))
			.sample();

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", 1L)
			.set("memberId", 1L).sample();

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			1L);

		ShipFishingPost savedShipFishingPost = fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", 1L)
			.set("subject", givenShipFishingPost.getSubject())
			.set("content", givenShipFishingPost.getContent())
			.sample();

		// When
		when(shipRepository.findById(1L)).thenReturn(Optional.of(givenShip));
		when(fishRepository.findAllById(givenRequestDto.fishList())).thenReturn(List.of(Fish.builder().build()));
		when(shipFishingPostRepository.save(givenShipFishingPost)).thenReturn(savedShipFishingPost);

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

		ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto, 1L);

		// When

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.saveShipFishingPost(givenRequestDto, 1L))
			.isInstanceOf(ShipException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipErrorCode.SHIP_NOT_FOUND)
			.hasMessageContaining(ShipErrorCode.SHIP_NOT_FOUND.getMessage());

	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [SHIP_MISMATCH_MEMBER_ID] [Service] - Fail")
	void t03() {
		// Given
		ShipFishingPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				ShipFishingPostRequest.Create.class)
			.set("shipId", 1L)
			.sample();

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", 1L)
			.set("memberId", 2L)
			.sample();

		ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			1L);

		// When
		when(shipRepository.findById(1L)).thenReturn(Optional.of(givenShip));

		// Then
		assertThatThrownBy(() -> shipFishingPostServiceImpl.saveShipFishingPost(givenRequestDto, 1L))
			.isInstanceOf(ShipException.class)
			.hasFieldOrPropertyWithValue("errorCode", ShipErrorCode.SHIP_MISMATCH_MEMBER_ID)
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
			.hasFieldOrPropertyWithValue("errorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
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
			.hasFieldOrPropertyWithValue("errorCode", ShipFishingPostErrorCode.POSTS_NOT_FOUND)
			.hasMessageContaining(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("선상 낚시 게시글 페이징 조회 [Service] - Success")
	void t08() {
		// Given
		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		GlobalRequest.PageRequest givenPageRequestDto = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", 10)
			.set("page", 0)
			.set("order", "DESC")
			.set("sort", "createdAt")
			.sample();

		List<ShipFishingPostResponse.DetailPage> givenResponseDto = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPostResponse.DetailPage.class)
			.sampleList(5);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> sliceResult = new SliceImpl<>(givenResponseDto, pageable, false);

		// When
		when(shipFishingPostRepository.findAllBySearchAndCondition(givenRequestDto, pageable))
			.thenReturn(sliceResult);

		Slice<ShipFishingPostResponse.DetailPage> resultPage = shipFishingPostServiceImpl.getShipFishingPostPage(
			givenRequestDto, givenPageRequestDto);

		// Then
		verify(shipFishingPostRepository, times(1)).findAllBySearchAndCondition(givenRequestDto, pageable);
		assertThat(resultPage.getContent().size()).isEqualTo(5);
		assertThat(resultPage.getNumber()).isEqualTo(0);
		assertThat(resultPage.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 페이징 조회 [조건 일부 null 값] [Service] - Success")
	void t09() {
		// Given
		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.minPrice(20000L)
			.maxPrice(null)
			.minRating(null)
			.location("부산")
			.fishId(null)
			.keyword(null)
			.build();

		GlobalRequest.PageRequest givenPageRequestDto = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", 10)
			.set("page", 0)
			.set("order", "DESC")
			.set("sort", "createdAt")
			.sample();

		List<ShipFishingPostResponse.DetailPage> givenResponseDto = fixtureMonkeyBuilder.giveMeBuilder(
			ShipFishingPostResponse.DetailPage.class).sampleList(5);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> sliceResult = new SliceImpl<>(givenResponseDto, pageable, false);

		when(shipFishingPostRepository.findAllBySearchAndCondition(givenRequestDto, pageable))
			.thenReturn(sliceResult);

		// When
		Slice<ShipFishingPostResponse.DetailPage> resultPage = shipFishingPostServiceImpl.getShipFishingPostPage(
			givenRequestDto, givenPageRequestDto);

		// Then
		verify(shipFishingPostRepository, times(1)).findAllBySearchAndCondition(givenRequestDto, pageable);
		assertThat(resultPage.getContent().size()).isEqualTo(5);
		assertThat(resultPage.getNumber()).isEqualTo(0);
		assertThat(resultPage.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 페이징 조회 [조건에 일치하는 데이터 없음] [Service] - Success")
	void t10() {
		// Given
		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.minPrice(20000L)
			.maxPrice(null)
			.minRating(null)
			.location("부산")
			.fishId(null)
			.keyword(null)
			.build();

		GlobalRequest.PageRequest givenPageRequestDto = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", 10)
			.set("page", 0)
			.set("order", "DESC")
			.set("sort", "createdAt")
			.sample();

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		List<ShipFishingPostResponse.DetailPage> givenResponseDto = Collections.emptyList();

		Slice<ShipFishingPostResponse.DetailPage> sliceResult = new SliceImpl<>(givenResponseDto, pageable, false);

		when(shipFishingPostRepository.findAllBySearchAndCondition(givenRequestDto, pageable))
			.thenReturn(sliceResult);

		// When
		Slice<ShipFishingPostResponse.DetailPage> resultPage = shipFishingPostServiceImpl.getShipFishingPostPage(
			givenRequestDto, givenPageRequestDto);

		// Then
		verify(shipFishingPostRepository, times(1)).findAllBySearchAndCondition(givenRequestDto, pageable);
		assertThat(resultPage.getContent()).isEmpty();
		assertThat(resultPage.getNumber()).isEqualTo(0);
		assertThat(resultPage.hasNext()).isEqualTo(false);
	}
}

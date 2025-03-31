package com.backend.domain.fishencyclopedia.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

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

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.converter.FishEncyclopediaConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaErrorCode;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class FishEncyclopediaServiceTest extends BaseTest {

	@Mock
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private FishRepository fishRepository;

	@InjectMocks
	private FishEncyclopediaServiceImpl fishEncyclopediasService;

	@Test
	@DisplayName("물고기 도감 저장 [Service] - Success")
	void t01() {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFromFishEncyclopedia = FishEncyclopediaConverter.fromFishEncyclopediasRequestCreate(
			givenCreate,
			givenMember.getMemberId()
		);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishEncyclopediaId", 1L)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		when(fishEncyclopediaRepository.createFishEncyclopedia(givenFromFishEncyclopedia)).thenReturn(givenFishEncyclopedia);
		when(fishPointRepository.existsById(givenFishEncyclopedia.getFishPointId())).thenReturn(true);
		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(true);

		// When
		Long savedId = fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId());

		// Then
		assertThat(savedId).isEqualTo(givenFishEncyclopedia.getFishEncyclopediaId());
		verify(fishEncyclopediaRepository, times(1)).createFishEncyclopedia(givenFromFishEncyclopedia);
	}

	@Test
	@DisplayName("물고기 도감 저장 [FishPoint Not Exists] [Service] - Fail")
	void t02() {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(true);
		when(fishPointRepository.existsById(givenFishEncyclopedia.getFishPointId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId()))
			.isExactlyInstanceOf(FishEncyclopediaException.class)
			.hasMessage(FishEncyclopediaErrorCode.NOT_EXISTS_FISH_POINT.getMessage());
	}

	@Test
	@DisplayName("물고기 도감 저장 [Fish Not Exists] [Service] - Fail")
	void t03() {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId()))
			.isExactlyInstanceOf(FishEncyclopediaException.class)
			.hasMessage(FishEncyclopediaErrorCode.NOT_EXISTS_FISH.getMessage());
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 [Service] - Success")
	void t04() {
		// Given
		FishEncyclopediaRequest.PageRequest givenPageRequest = fixtureMonkeyRecord
			.giveMeBuilder(FishEncyclopediaRequest.PageRequest.class)
			.set("size", 10)
			.set("page", 0)
			.sample();

		Fish givenFish = fixtureMonkeyBuilder.giveMeOne(Fish.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		List<FishEncyclopediaResponse.Detail> givenDetailList = fixtureMonkeyBuilder
			.giveMeBuilder(FishEncyclopediaResponse.Detail.class)
			.sampleList(7);
		Pageable givenPageable = PageRequest.of(givenPageRequest.page(), givenPageRequest.size());

		boolean givenHasNext = false;

		SliceImpl<FishEncyclopediaResponse.Detail> givenSlice = new SliceImpl<>(
			givenDetailList,
			givenPageable,
			givenHasNext
		);

		when(fishEncyclopediaRepository.findDetailByAllByFishPointIdAndFishId(
			givenPageRequest,
			givenFish.getFishId(),
			givenMember.getMemberId())).thenReturn(givenSlice);

		// When
		Slice<FishEncyclopediaResponse.Detail> getDetailList = fishEncyclopediasService.getDetailList(givenPageRequest,
			givenFish.getFishId(), givenMember.getMemberId());

		// Then
		assertThat(getDetailList.getContent()).hasSize(givenDetailList.size());
		assertThat(getDetailList.getContent()).isEqualTo(givenDetailList);
		assertThat(getDetailList.hasNext()).isFalse();
	}
}
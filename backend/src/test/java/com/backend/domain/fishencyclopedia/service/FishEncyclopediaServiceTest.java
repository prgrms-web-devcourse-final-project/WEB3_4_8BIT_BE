package com.backend.domain.fishencyclopedia.service;

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

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;
import com.backend.domain.catchmaxlength.repository.CatchMaxLengthRepository;
import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaErrorCode;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class FishEncyclopediaServiceTest extends BaseTest {

	@Mock
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private FishRepository fishRepository;

	@Mock
	private CatchMaxLengthRepository catchMaxLengthRepository;

	@InjectMocks
	private FishEncyclopediaServiceImpl fishEncyclopediasService;

	@Test
	@DisplayName("물고기 도감 저장 [Service] - Success")
	void t01() {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation
			.giveMeBuilder(FishEncyclopediaRequest.Create.class)
			.set("length", 5)
			.sample();

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishEncyclopediaId", 1L)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		ArbitraryBuilder<CatchMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
			.giveMeBuilder(CatchMaxLength.class)
			.set("catchMaxLengthId", 1L)
			.set("fishId", givenCreate.fishId())
			.set("memberId", givenMember.getMemberId())
			.set("bestLength", 10);

		when(fishPointRepository.existsById(givenFishEncyclopedia.getFishPointId())).thenReturn(true);
		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(true);
		when(catchMaxLengthRepository
			.findByFishIdAndMemberId(givenCreate.fishId(), givenMember.getMemberId()))
			.thenReturn(Optional.of(arbitraryBuilder.sample()));

		when(fishEncyclopediaRepository.createFishEncyclopedia(any(FishEncyclopedia.class)))
			.thenReturn(givenFishEncyclopedia);

		// When
		Long savedId = fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId());

		// Then
		assertThat(savedId).isEqualTo(givenFishEncyclopedia.getFishEncyclopediaId());
		verify(fishEncyclopediaRepository, times(1)).createFishEncyclopedia(any(FishEncyclopedia.class));
	}

	@Test
	@DisplayName("물고기 도감 저장 [FishEncyclopediaMaxLength Empty] [Service] - Success")
	void t02() {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishEncyclopediaId", 1L)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		ArbitraryBuilder<CatchMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
			.giveMeBuilder(CatchMaxLength.class)
			.set("catchMaxLengthId", null)
			.set("fishId", givenCreate.fishId())
			.set("memberId", givenMember.getMemberId())
			.set("bestLength", givenCreate.length());

		when(fishEncyclopediaRepository.createFishEncyclopedia(any(FishEncyclopedia.class)))
			.thenReturn(givenFishEncyclopedia);
		when(fishPointRepository.existsById(givenFishEncyclopedia.getFishPointId())).thenReturn(true);
		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(true);
		when(catchMaxLengthRepository
			.findByFishIdAndMemberId(givenCreate.fishId(), givenMember.getMemberId()))
			.thenReturn(Optional.empty());
		when(catchMaxLengthRepository.save(any(CatchMaxLength.class)))
			.thenReturn(arbitraryBuilder.set("fishEncyclopediaMaxLengthId", 1L).sample());

		// When
		Long savedId = fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId());

		// Then
		assertThat(savedId).isEqualTo(givenFishEncyclopedia.getFishEncyclopediaId());
		verify(fishEncyclopediaRepository, times(1)).createFishEncyclopedia(any(FishEncyclopedia.class));
		verify(catchMaxLengthRepository, times(1)).save(any(CatchMaxLength.class));
	}



	@Test
	@DisplayName("물고기 도감 저장 [FishPoint Not Exists] [Service] - Fail")
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

		when(fishRepository.existsById(givenFishEncyclopedia.getFishId())).thenReturn(true);
		when(fishPointRepository.existsById(givenFishEncyclopedia.getFishPointId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(
			() -> fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId()))
			.isExactlyInstanceOf(FishEncyclopediaException.class)
			.hasMessage(FishEncyclopediaErrorCode.NOT_EXISTS_FISH_POINT.getMessage());
	}

	@Test
	@DisplayName("물고기 도감 저장 [Fish Not Exists] [Service] - Fail")
	void t04() {
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
		assertThatThrownBy(
			() -> fishEncyclopediasService.createFishEncyclopedia(givenCreate, givenMember.getMemberId()))
			.isExactlyInstanceOf(FishEncyclopediaException.class)
			.hasMessage(FishEncyclopediaErrorCode.NOT_EXISTS_FISH.getMessage());
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 [Service] - Success")
	void t05() {
		// Given
		GlobalRequest.CursorRequest givenCursorRequestDto = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.CursorRequest.class)
			.set("size", 10)
			.sample();

		Fish givenFish = fixtureMonkeyBuilder.giveMeOne(Fish.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		List<FishEncyclopediaResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(FishEncyclopediaResponse.Detail.class)
			.sampleList(7);

		boolean givenHasNext = false;

		ScrollResponse<FishEncyclopediaResponse.Detail> givenScrollResponse = ScrollResponse.from(
			givenDetailList,
			givenCursorRequestDto.size(),
			givenDetailList.size(),
			true,
			givenHasNext
		);

		when(fishEncyclopediaRepository.findDetailByAllByMemberIdAndFishId(
			givenCursorRequestDto,
			givenFish.getFishId(),
			givenMember.getMemberId())).thenReturn(givenScrollResponse);

		// When
		ScrollResponse<FishEncyclopediaResponse.Detail> getDetailList = fishEncyclopediasService.getDetailList(givenCursorRequestDto,
			givenFish.getFishId(), givenMember.getMemberId());

		// Then
		assertThat(getDetailList.content()).hasSize(givenDetailList.size());
		assertThat(getDetailList.content()).isEqualTo(givenDetailList);
		assertThat(getDetailList.isLast()).isFalse();
	}
}
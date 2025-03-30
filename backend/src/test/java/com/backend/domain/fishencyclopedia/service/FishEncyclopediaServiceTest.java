package com.backend.domain.fishencyclopedia.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fishencyclopedia.converter.FishEncyclopediaConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.Util.BaseTest;

@ExtendWith(MockitoExtension.class)
class FishEncyclopediaServiceTest extends BaseTest {

	@Mock
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@InjectMocks
	private FishEncyclopediaServiceImpl fishEncyclopediasService;

	@Test
	@DisplayName("물고기 도감 저장 [Service] - Success")
	void t01() {
		//given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeOne(Member.class);

		FishEncyclopedia givenFromFishEncyclopedia = FishEncyclopediaConverter.fromFishEncyclopediasRequestCreate(
			givenCreate,
			givenMember.getMemberId()
		);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMember.getMemberId())
			.sample();

		Mockito.when(fishEncyclopediaRepository.save(givenFromFishEncyclopedia)).thenReturn(givenFishEncyclopedia);

		//when
		Long savedId = fishEncyclopediasService.save(givenCreate, givenMember.getMemberId());

		//then
		assertThat(savedId).isEqualTo(givenFishEncyclopedia.getFishEncyclopediaId());
		verify(fishEncyclopediaRepository, times(1)).save(givenFromFishEncyclopedia);
	}

	@Test
	@DisplayName("물고기 도감 저장 [Fish Not Exists] [Service] - Fail")
	void t02() {
		//given

		//when

		//then
	}

	@Test
	@DisplayName("물고기 도감 저장 [FishPoint Not Exists] [Service] - Fail")
	void t03() {
		//given

		//when

		//then

	}
}
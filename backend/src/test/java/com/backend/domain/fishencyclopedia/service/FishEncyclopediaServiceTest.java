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
import com.backend.domain.member.entity.Members;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

@ExtendWith(MockitoExtension.class)
class FishEncyclopediaServiceTest {

	@Mock
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@InjectMocks
	private FishEncyclopediaServiceImpl fishEncyclopediasService;

	private final FixtureMonkey fixtureMonkeyValidation;
	private final FixtureMonkey fixtureMonkeyBuilder;

	public FishEncyclopediaServiceTest() {

		fixtureMonkeyValidation = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE) // 생성자 기반
			.defaultNotNull(true) // 제외시 필드 null로 초기화
			.plugin(new JakartaValidationPlugin()) //validation에 맞는 객체 생성
			.build();

		fixtureMonkeyBuilder = FixtureMonkey.builder()
			.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	}

	@Test
	@DisplayName("물고기 도감 저장 [Service] - Success")
	void t01() {
		//given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class);

		Members givenMembers = fixtureMonkeyBuilder.giveMeOne(Members.class);

		FishEncyclopedia givenFromFishEncyclopedia = FishEncyclopediaConverter.fromFishEncyclopediasRequestCreate(
			givenCreate,
			givenMembers.getMemberId()
		);

		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeBuilder(FishEncyclopedia.class)
			.set("fishId", givenCreate.fishId())
			.set("length", givenCreate.length())
			.set("fishPointId", givenCreate.fishPointId())
			.set("memberId", givenMembers.getMemberId())
			.sample();

		Mockito.when(fishEncyclopediaRepository.save(givenFromFishEncyclopedia)).thenReturn(givenFishEncyclopedia);

		//when
		Long savedId = fishEncyclopediasService.save(givenCreate, givenMembers);

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
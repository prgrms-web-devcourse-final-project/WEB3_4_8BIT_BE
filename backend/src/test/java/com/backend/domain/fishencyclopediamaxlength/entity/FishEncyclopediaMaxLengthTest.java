package com.backend.domain.fishencyclopediamaxlength.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

class FishEncyclopediaMaxLengthTest extends BaseTest {

	ArbitraryBuilder<FishEncyclopediaMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishEncyclopediaMaxLength.class)
		.set("bestLength", 10);

	@Test
	@DisplayName("물고기 최대 길이 설정 성공 테스트 [givenLength > BestLength] [Entity] - Success")
	void t01() {
		// Given
		FishEncyclopediaMaxLength givenFishEncyclopediaMaxLength = arbitraryBuilder.sample();
		Integer givenLength = 20;

		// When
		givenFishEncyclopediaMaxLength.setBestLength(givenLength);

		// Then
		assertThat(givenFishEncyclopediaMaxLength.getBestLength()).isEqualTo(givenLength);
	}

	@Test
	@DisplayName("물고기 최대 길이 설정 성공 테스트 [givenLength < BestLength] [Entity] - Success")
	void t02() {
		// Given
		FishEncyclopediaMaxLength givenFishEncyclopediaMaxLength = arbitraryBuilder.sample();
		Integer givenLength = 5;

		// When
		givenFishEncyclopediaMaxLength.setBestLength(givenLength);

		// Then
		assertThat(givenFishEncyclopediaMaxLength.getBestLength()).isEqualTo(10);
	}
}
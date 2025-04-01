package com.backend.domain.catchmaxlength.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

class CatchMaxLengthTest extends BaseTest {

	ArbitraryBuilder<CatchMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(CatchMaxLength.class)
		.set("bestLength", 10);

	@Test
	@DisplayName("물고기 최대 길이 설정 성공 테스트 [givenLength > BestLength] [Entity] - Success")
	void t01() {
		// Given
		CatchMaxLength givenCatchMaxLength = arbitraryBuilder.sample();
		Integer givenLength = 20;

		// When
		givenCatchMaxLength.setBestLength(givenLength);

		// Then
		assertThat(givenCatchMaxLength.getBestLength()).isEqualTo(givenLength);
	}

	@Test
	@DisplayName("물고기 최대 길이 설정 성공 테스트 [givenLength < BestLength] [Entity] - Success")
	void t02() {
		// Given
		CatchMaxLength givenCatchMaxLength = arbitraryBuilder.sample();
		Integer givenLength = 5;

		// When
		givenCatchMaxLength.setBestLength(givenLength);

		// Then
		assertThat(givenCatchMaxLength.getBestLength()).isEqualTo(10);
	}
}
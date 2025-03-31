package com.backend.domain.fishencyclopediamaxlength.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishencyclopediamaxlength.entity.FishEncyclopediaMaxLength;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({FishEncyclopediaMaxLengthRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
@Slf4j
class FishEncyclopediaMaxLengthRepositoryTest extends BaseTest {

	@Autowired
	private FishEncyclopediaMaxLengthRepository fishEncyclopediaMaxLengthRepository;

	ArbitraryBuilder<FishEncyclopediaMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishEncyclopediaMaxLength.class)
		.set("fishEncyclopediaMaxLengthId", null);

	@Test
	@DisplayName("물고기 도감 최대 길이 저장 [Repository] - Success")
	void t01() {
		// Given
		FishEncyclopediaMaxLength givenFishEncyclopediaMaxLength = arbitraryBuilder
			.sample();

		// When
		FishEncyclopediaMaxLength saveFishEncyclopediaMaxLength = fishEncyclopediaMaxLengthRepository
			.save(givenFishEncyclopediaMaxLength);

		// Then
		assertThat(saveFishEncyclopediaMaxLength.getFishEncyclopediaMaxLengthId()).isNotNull();
	}

	@Test
	@DisplayName("물고기 도감 최대 길이 조회 [Repository] - Success")
	void t02() {
		// Given
		FishEncyclopediaMaxLength givenFishEncyclopediaMaxLength = arbitraryBuilder
			.sample();

		FishEncyclopediaMaxLength savedFishEncyclopediaMaxLength = fishEncyclopediaMaxLengthRepository
			.save(givenFishEncyclopediaMaxLength);

		// When
		FishEncyclopediaMaxLength findFishEncyclopediaMaxLength = fishEncyclopediaMaxLengthRepository.findByFishIdAndMemberId(
			savedFishEncyclopediaMaxLength.getFishId(),
			savedFishEncyclopediaMaxLength.getMemberId()
		).get();

		// Then
		assertThat(findFishEncyclopediaMaxLength).isEqualTo(savedFishEncyclopediaMaxLength);

	}
}
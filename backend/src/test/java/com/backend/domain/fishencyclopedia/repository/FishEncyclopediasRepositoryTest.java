package com.backend.domain.fishencyclopedia.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;
import com.backend.global.config.JpaAuditingConfig;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;

@Slf4j
@Import(JpaAuditingConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class FishEncyclopediasRepositoryTest {

	private final FishEncyclopediaRepository fishEncyclopediaRepository;
	private final FixtureMonkey fixtureMonkeyBuilder;

	@Autowired
	public FishEncyclopediasRepositoryTest(FishEncyclopediaRepository fishEncyclopediaRepository) {
		this.fishEncyclopediaRepository = fishEncyclopediaRepository;

		fixtureMonkeyBuilder = FixtureMonkey.builder()
			.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	}

	@Test
	@DisplayName("물고기 도감 저장 [Repository] - Success")
	void t01() {

		//given
		FishEncyclopedias givenFishEncyclopedias = fixtureMonkeyBuilder
			.giveMeBuilder(FishEncyclopedias.class)
			.set("fishEncyclopediaId", null)
			.sample();

		//when
		FishEncyclopedias savedFishEncyclopedias = fishEncyclopediaRepository.save(givenFishEncyclopedias);

		//then
		assertThat(savedFishEncyclopedias).isNotNull();
	}
}
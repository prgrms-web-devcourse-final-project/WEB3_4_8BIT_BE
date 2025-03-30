package com.backend.domain.fishencyclopedia.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;

@Import({FishEncyclopediaRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
class FishEncyclopediaRepositoryTest extends BaseTest {

	@Autowired
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@Test
	@DisplayName("물고기 도감 저장 [Repository] - Success")
	void t01() {
		//given
		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder
			.giveMeBuilder(FishEncyclopedia.class)
			.set("fishEncyclopediaId", null)
			.sample();

		//when
		FishEncyclopedia savedFishEncyclopedia = fishEncyclopediaRepository.save(givenFishEncyclopedia);

		//then
		assertThat(savedFishEncyclopedia.getFishEncyclopediaId()).isNotNull();
	}
}
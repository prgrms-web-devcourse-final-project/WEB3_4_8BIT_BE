package com.backend.domain.fish.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fish.entity.Fish;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({FishRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
class FishRepositoryTest extends BaseTest {

	@Autowired
	private FishRepository fishRepository;

	final Arbitrary<String> englishString = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(50);

	final ArbitraryBuilder<Fish> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
		.set("name", englishString)
		.set("icon", englishString)
		.set("spawnLocation", englishString);

	@Test
	@DisplayName("물고기 저장 [Repository] - Success")
	void t01() {
		// Given
		Fish givenFish = arbitraryBuilder.set("fishId", null).sample();

		// When
		Fish savedFish = fishRepository.save(givenFish);

		// Then
		assertThat(savedFish.getFishId()).isNotNull();
	}

	@Test
	@DisplayName("물고기 존재 여부 조회 [Repository] - Success")
	void t02() {
		// Given
		Fish gienFish = arbitraryBuilder.set("fishId", null).sample();
		Fish savedFish = fishRepository.save(gienFish);

		// When
		boolean existsFish = fishRepository.existsById(savedFish.getFishId());

		// Then
		assertThat(existsFish).isTrue();
	}
}
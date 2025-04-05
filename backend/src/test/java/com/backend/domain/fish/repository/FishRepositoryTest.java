package com.backend.domain.fish.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.entity.Fish;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageJpaRepository;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({FishRepositoryImpl.class, FishQueryRepository.class, JpaAuditingConfig.class, QuerydslConfig.class})
@DataJpaTest
class FishRepositoryTest extends BaseTest {

	@Autowired
	private FishRepository fishRepository;

	@Autowired
	private StorageJpaRepository storageJpaRepository;

	final Arbitrary<String> englishString = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(30);

	final ArbitraryBuilder<Fish> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
		.set("name", englishString)
		.set("icon", englishString)
		.set("spawnLocation", englishString);
	@Autowired
	private FishJpaRepository fishJpaRepository;

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

	@Test
	@DisplayName("Id List 에 포함된 물고기 조회 [Repository] - Success")
	void t03() {
		// Given
		List<Long> fishIdList = new ArrayList<>();
		arbitraryBuilder.set("fishId", null).sampleStream().limit(5)
			.forEach((fish) -> {
				fishIdList.add(fishRepository.save(fish).getFishId());
			});

		// When
		List<Fish> fishList = fishRepository.findAllById(fishIdList);

		// Then
		assertThat(fishList).hasSize(5);
	}

	@Test
	@DisplayName("물고기 상세 조회 [Repository] - Success")
	void t04() {
		// Given
		File givenFile = fixtureMonkeyBuilder.giveMeBuilder(File.class)
			.set("fileId", null)
			.sample();

		File savedFile = storageJpaRepository.save(givenFile);

		Fish givenFish = arbitraryBuilder
			.set("fishId", null)
			.set("fileId", savedFile.getFileId())
			.sample();


		Fish savedFish = fishRepository.save(givenFish);

		// When
		FishResponse.Detail findDetail = fishRepository.findDetailById(savedFish.getFishId()).orElse(null);

		// Then
		assertThat(findDetail).isNotNull();
		assertThat(findDetail.fishId()).isEqualTo(savedFish.getFishId());
	}

	@Test
	@DisplayName("물고기 인기순 조회 [Repository] - Success")
	void t06() {
		// Given
		List<Fish> givenFishList = arbitraryBuilder.set("fishId", null).sampleList(10);
		List<Fish> savedFishList = fishJpaRepository.saveAll(givenFishList);

		// When
		List<FishResponse.Popular> findPopular = fishRepository.findPopular(10);
		// Then
		List<Fish> sortedFishList = savedFishList.stream()
			.sorted(Comparator.comparing(Fish::getPopularityScore).reversed())
			.toList();

		// Then
		assertThat(findPopular).hasSize(10);
		assertThat(findPopular.get(0).popularityScore()).isEqualTo(sortedFishList.get(0).getPopularityScore());
	}
}
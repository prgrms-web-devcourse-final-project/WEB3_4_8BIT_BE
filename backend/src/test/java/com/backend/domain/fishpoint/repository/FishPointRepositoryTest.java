package com.backend.domain.fishpoint.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({FishPointRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
class FishPointRepositoryTest extends BaseTest {

	@Autowired
	private FishPointRepository fishPointRepository;

	final ArbitraryBuilder<FishPoint> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(FishPoint.class)
		.size("fishPointName", 1, 49)
		.size("fishPointDetailName", 1, 49);

	@Test
	@DisplayName("낚시 포인트 저장 [Repository] - Success")
	void t01() {
		// Given
		FishPoint givenFishPoint = arbitraryBuilder.set("fishPointId", null).sample();

		// When
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// Then
		assertThat(savedFishPoint.getFishPointId()).isNotNull();
	}

	@Test
	@DisplayName("낚시 포인트 존재 여부 조회 [Repository] - Success")
	void t02() {
		// Given
		FishPoint givenFishPoint = arbitraryBuilder.set("fishPointId", null).sample();
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// When
		boolean existsFishPoint = fishPointRepository.existsById(savedFishPoint.getFishPointId());

		// Then
		assertThat(existsFishPoint).isTrue();
	}
}
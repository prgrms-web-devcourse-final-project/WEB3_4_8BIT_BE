package com.backend.domain.fishpoint.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;

@DataJpaTest
@Import({
	FishPointRepositoryImpl.class,
	FishPointQueryRepository.class,
	JpaAuditingConfig.class,
	QuerydslConfig.class})
class FishPointRepositoryTest extends BaseTest {

	@Autowired
	private FishPointRepository fishPointRepository;

	@Test
	@DisplayName("낚시 포인트 저장 [Repository] - Success")
	void t01() {
		// Given
		FishPoint givenFishPoint = createRandomFishPoint();

		// When
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// Then
		assertThat(savedFishPoint.getFishPointId()).isNotNull();
	}

	@Test
	@DisplayName("낚시 포인트 존재 여부 조회 [Repository] - Success")
	void t02() {
		// Given
		FishPoint givenFishPoint = createRandomFishPoint();
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// When
		boolean existsFishPoint = fishPointRepository.existsById(savedFishPoint.getFishPointId());

		// Then
		assertThat(existsFishPoint).isTrue();
	}
}
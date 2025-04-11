package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

@DataJpaTest
@Import({
	QuerydslConfig.class,
	JpaAuditingConfig.class,
	FishPointSummaryRepositoryImpl.class,
	FishPointSummaryQueryRepository.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FishPointSummaryRepositoryTest extends BaseTest {

	@Autowired
	private FishPointSummaryRepository fishPointSummaryRepository;

	@Autowired
	private FishPointSummaryJpaRepository fishPointSummaryJpaRepository;

	private FishPointSummary createSummary(Long fishPointId, Long fishId, Integer count) {
		return fixtureMonkeyBuilder.giveMeBuilder(FishPointSummary.class)
			.set("fishPointSummaryId", null)
			.set("fishPointId", fishPointId)
			.set("fishId", fishId)
			.set("fileId", fishId)
			.set("totalCount", count)
			.sample();
	}

	@Test
	@DisplayName("낚시 포인트 ID로 총 잡힌 수 기준 Top 4 조회 [Repository] - Success")
	void findTop4ByFishPointIdOrderByTotalCountDescTest() {
		// given
		Long fishPointId = 100L;

		// 5개 저장, totalCount 기준 정렬
		fishPointSummaryJpaRepository.saveAll(List.of(
			createSummary(fishPointId, 1L, 50),
			createSummary(fishPointId, 2L, 30),
			createSummary(fishPointId, 3L, 70),
			createSummary(fishPointId, 4L, 10),
			createSummary(fishPointId, 5L, 40)
		));

		// when
		List<Basic> result = fishPointSummaryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(fishPointId);

		// then
		assertThat(result).hasSize(4);
		assertThat(result.get(0).totalCount()).isEqualTo(70);
		assertThat(result.get(1).totalCount()).isEqualTo(50);
		assertThat(result.get(2).totalCount()).isEqualTo(40);
		assertThat(result.get(3).totalCount()).isEqualTo(30);
	}

	@Test
	@DisplayName("fishPointId, fishId 목록 조건으로 집계 조회 [Repository] - Success")
	void findByFishPointIdInAndFishIdInTest() {
		// given
		fishPointSummaryJpaRepository.saveAll(List.of(
			createSummary(1L, 10L, 5), // 포함
			createSummary(1L, 20L, 3), // 포함
			createSummary(2L, 10L, 7), // 포함
			createSummary(3L, 30L, 1), // 제외
			createSummary(4L, 40L, 2)  // 제외
		));

		Set<Long> fishPointIds = Set.of(1L, 2L);
		Set<Long> fishIds = Set.of(10L, 20L);

		// when
		List<FishPointSummary> result = fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(
			fishPointIds,
			fishIds
		);

		// then
		assertThat(result).hasSize(3);
	}

	@Test
	@DisplayName("낚시 포인트 집계 저장 [Repository] - Success")
	void saveAllTest() {
		// given
		FishPointSummary summary1 = createSummary(1L, 10L, 5);
		FishPointSummary summary2 = createSummary(1L, 8L, 1);
		FishPointSummary summary3 = createSummary(2L, 8L, 1);

		// when
		fishPointSummaryRepository.saveAll(List.of(summary1, summary2, summary3));

		// then
		List<FishPointSummary> result = fishPointSummaryJpaRepository.findAll();
		assertThat(result).hasSize(3);
		assertThat(result.get(0).getTotalCount()).isEqualTo(5);
		assertThat(result.get(1).getTotalCount()).isEqualTo(1);
		assertThat(result.get(2).getTotalCount()).isEqualTo(1);
	}
}
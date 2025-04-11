package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;
import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.repository.FishingTripPostJpaRepository;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FishPointRepositoryTest extends BaseTest {

	@Autowired
	private FishPointRepository fishPointRepository;

	@Autowired
	private FishPointJpaRepository fishPointJpaRepository;

	@Autowired
	private FishingTripPostJpaRepository fishingTripPostJpaRepository;

	private FishingTripPost createTestPost(Long pointId) {
		return fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", null)
			.set("fishingPointId", pointId)
			.set("subject", "동출 게시글")
			.set("currentCount", 0)
			.set("recruitmentCount", 5)
			.set("fishingDate", ZonedDateTime.now())
			.set("isShipFish", false)
			.set("memberId", 1L)
			.sample();
	}

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

	@Test
	@DisplayName("지역 ID로 낚시 포인트 조회 [Repository] - Success")
	void t03() {
		// given
		fishPointJpaRepository.saveAll(List.of(
			createRandomFishPoint(13L, "전라남도 고흥군 동일면"),
			createRandomFishPoint(13L, "전라남도 고흥군 동일면"),
			createRandomFishPoint(16L, "제주특별자치도 제주시 한림읍")
		));

		// when
		List<Basic> result = fishPointRepository.findByRegionId(13L);

		// then
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("낚시 포인트 이름으로 부분 조회 [Repository] - Success")
	void t04() {
		// given
		fishPointJpaRepository.saveAll(List.of(
			createRandomFishPoint(13L, "전라남도 고흥군 동일면"),
			createRandomFishPoint(13L, "전라남도 고흥군 동일면"),
			createRandomFishPoint(16L, "제주특별자치도 제주시 한림읍")
		));

		// when
		List<Basic> result = fishPointRepository.findByFishPointName("전라남도");

		// then
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("인기 낚시 포인트 상위 3개 조회 [Repository] - Success")
	void t05() {
		// given
		FishPoint givenPoint1 = fishPointJpaRepository.save(createRandomFishPoint(13L, "전라남도 고흥군 동일면"));
		FishPoint givenPoint2 = fishPointJpaRepository.save(createRandomFishPoint(13L, "전라남도 고흥군 동일면"));
		FishPoint givenPoint3 = fishPointJpaRepository.save(createRandomFishPoint(16L, "제주특별자치도 제주시 한림읍"));
		FishPoint givenPoint4 = fishPointJpaRepository.save(createRandomFishPoint(16L, "제주특별자치도 서귀포시 성산읍"));

		// 동출 게시글 수: p1 = 3개, p2 = 2개, p3 = 1개, p4 = 0개
		fishingTripPostJpaRepository.saveAll(List.of(
			createTestPost(givenPoint1.getFishPointId()),
			createTestPost(givenPoint1.getFishPointId()),
			createTestPost(givenPoint1.getFishPointId()),

			createTestPost(givenPoint2.getFishPointId()),
			createTestPost(givenPoint2.getFishPointId()),

			createTestPost(givenPoint3.getFishPointId())
		));

		// when
		List<Popularity> result = fishPointRepository.findPopularityFishPoints();

		// then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).fishPointName()).isEqualTo(givenPoint1.getFishPointName());
		assertThat(result.get(0).recruitmentCount()).isEqualTo(3L);
		assertThat(result.get(1).recruitmentCount()).isEqualTo(2L);
		assertThat(result.get(2).recruitmentCount()).isEqualTo(1L);
	}

	@Test
	@DisplayName("낚시 포인트 ID로 단건 조회 [Repository] - Success")
	void t06() {
		// given
		FishPoint givenPoint = fishPointRepository.save(createRandomFishPoint(16L, "제주특별자치도 제주시 한림읍"));

		// when
		Optional<FishPoint> fishPoint = fishPointRepository.findByFishPointId(givenPoint.getFishPointId());

		// then
		assertThat(fishPoint).isPresent();
		assertThat(fishPoint.get().getFishPointName()).isEqualTo(givenPoint.getFishPointName());
	}
}
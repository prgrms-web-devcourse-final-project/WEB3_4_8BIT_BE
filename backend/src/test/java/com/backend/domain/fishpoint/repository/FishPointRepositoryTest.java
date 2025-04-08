package com.backend.domain.fishpoint.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({
	FishPointRepositoryImpl.class,
	FishPointQueryRepository.class,
	JpaAuditingConfig.class,
	QuerydslConfig.class})
class FishPointRepositoryTest extends BaseTest {

	@Autowired
	private FishPointRepository fishPointRepository;

	final Arbitrary<String> englishString = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(50);

	final ArbitraryBuilder<FishPoint> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(FishPoint.class)
		.set("fishPointName", englishString)
		.set("fishPointDetailName", englishString);

	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	private FishPoint createFishPoint(String name, double lat, double lng, boolean isBan, Long regionId) {
		Point location = geometryFactory.createPoint(new Coordinate(lng, lat));
		location.setSRID(4326);

		return FishPoint.builder()
			.fishPointName(name)
			.fishPointDetailName(name + " 상세")
			.latitude(lat)
			.longitude(lng)
			.location(location)
			.isBan(isBan)
			.regionId(regionId)
			.build();
	}

	@Test
	@DisplayName("낚시 포인트 저장 [Repository] - Success")
	void t01() {
		// Given
		FishPoint givenFishPoint = createFishPoint("테스트포인트", 37.0, 127.0, false, 1L);

		// When
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// Then
		assertThat(savedFishPoint.getFishPointId()).isNotNull();
	}

	@Test
	@DisplayName("낚시 포인트 존재 여부 조회 [Repository] - Success")
	void t02() {
		// Given
		FishPoint givenFishPoint = createFishPoint("존재포인트", 37.1, 127.1, false, 1L);
		FishPoint savedFishPoint = fishPointRepository.save(givenFishPoint);

		// When
		boolean existsFishPoint = fishPointRepository.existsById(savedFishPoint.getFishPointId());

		// Then
		assertThat(existsFishPoint).isTrue();
	}
}
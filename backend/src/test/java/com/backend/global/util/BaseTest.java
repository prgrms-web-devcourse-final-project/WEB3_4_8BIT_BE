package com.backend.global.util;

import java.util.concurrent.ThreadLocalRandom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fishpoint.entity.FishPoint;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public abstract class BaseTest {

	public static final FixtureMonkey fixtureMonkeyBuilder  = FixtureMonkey.builder()
			.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	public static final FixtureMonkey fixtureMonkeyRecord = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	public static final FixtureMonkey fixtureMonkeyValidation = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.plugin(new JakartaValidationPlugin())
			.build();

	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	// 랜덤 FishPoint 생성 메서드
	protected FishPoint createRandomFishPoint() {
		String name = englishString.sample();
		String detailName = englishString.sample();
		double longitude = randomDouble(126.0, 130.0);
		double latitude = randomDouble(33.0, 39.0);
		long regionId = randomLong();

		Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
		location.setSRID(4326);

		return FishPoint.builder()
			.fishPointName(name)
			.fishPointDetailName(detailName)
			.longitude(longitude)
			.latitude(latitude)
			.location(location)
			.isBan(false)
			.regionId(regionId)
			.build();
	}

	private final Arbitrary<String> englishString = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(3).ofMaxLength(20);

	private double randomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	private long randomLong() {
		return ThreadLocalRandom.current().nextLong(1, (long)16 + 1);
	}

}

package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Repository;

import com.backend.domain.fishpoint.dto.response.QFishPointResponse_Basic;
import com.backend.domain.fishpoint.dto.response.QFishPointResponse_Popularity;
import com.backend.domain.fishpoint.dto.response.QFishPointResponse_WithDistance;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FishPointQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	public List<Basic> findByBounds(final double swLat, final double swLng, final double neLat, final double neLng) {

		String polygonWKT = String.format(
			"POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
			swLat, swLng,
			neLat, swLng,
			neLat, neLng,
			swLat, neLng,
			swLat, swLng
		);

		log.debug("polygonWKT: {}", polygonWKT);

		BooleanExpression withinExpr = Expressions.booleanTemplate(
			"ST_Within({0}, ST_GeomFromText({1}, 4326))",
			fishPoint.location,
			Expressions.constant(polygonWKT)
		);

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_Basic(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan
			))
			.from(fishPoint)
			.where(withinExpr.and(notBanned))
			.fetch();
	}

	public List<WithDistance> findByDistanceWithin(final double lat, final double lng, final double radiusKm) {
		Point center = geometryFactory.createPoint(new Coordinate(lng, lat));
		center.setSRID(4326);

		BooleanExpression withinRadius = Expressions.booleanTemplate(
			"ST_Distance({0}, {1}) <= {2}",
			fishPoint.location,
			center,
			radiusKm * 1000
		);

		NumberExpression<Double> distanceExpr = Expressions.numberTemplate(
			Double.class,
			"ST_Distance({0}, {1})",
			fishPoint.location,
			center
		);

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_WithDistance(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan,
				distanceExpr.divide(1000.0)
			))
			.from(fishPoint)
			.where(withinRadius.and(notBanned))
			.fetch();
	}

	public List<WithDistance> findByNearestFishPoints(final double lat, final double lng) {
		Point center = geometryFactory.createPoint(new Coordinate(lng, lat));
		center.setSRID(4326);

		NumberExpression<Double> distanceExpr = Expressions.numberTemplate(
			Double.class,
			"ST_Distance({0}, {1})",
			fishPoint.location,
			center
		);

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_WithDistance(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan,
				distanceExpr.divide(1000.0)
			))
			.from(fishPoint)
			.where(notBanned)
			.orderBy(distanceExpr.asc())
			.limit(3)
			.fetch();
	}

	public List<Basic> findByRegionId(final Long regionId) {

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_Basic(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan
			))
			.from(fishPoint)
			.where(fishPoint.regionId.eq(regionId).and(notBanned))
			.orderBy(fishPoint.fishPointId.asc())
			.fetch();
	}

	public List<Basic> findByFishPointName(final String fishPointName) {

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_Basic(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan
			))
			.from(fishPoint)
			.where(fishPoint.fishPointName.containsIgnoreCase(fishPointName).and(notBanned))
			.orderBy(fishPoint.fishPointId.asc())
			.fetch();
	}

	public List<Popularity> findPopularityFishPoints() {

		BooleanExpression notBanned = fishPoint.isBan.isFalse();

		return jpaQueryFactory
			.select(new QFishPointResponse_Popularity(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishingTripPost.fishingPointId.count()
			))
			.from(fishingTripPost)
			.join(fishPoint).on(fishingTripPost.fishingPointId.eq(fishPoint.fishPointId))
			.where(notBanned)
			.groupBy(fishPoint.fishPointId)
			.orderBy(fishingTripPost.fishingPointId.count().desc())
			.limit(3)
			.fetch();
	}
}

package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.dto.response.QFishPointResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FishPointQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<FishPointResponse> findByBounds(double swLat, double swLng, double neLat, double neLng) {

		String polygonWKT = String.format(
			"POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
			swLat, swLng,
			neLat, swLng,
			neLat, neLng,
			swLat, neLng,
			swLat, swLng
		);

		log.info("polygonWKT: {}", polygonWKT);

		BooleanExpression withinExpr = Expressions.booleanTemplate(
			"ST_Within({0}, ST_GeomFromText({1}, 4326))",
			fishPoint.location,
			Expressions.constant(polygonWKT)
		);

		return jpaQueryFactory
			.select(new QFishPointResponse(
				fishPoint.fishPointId,
				fishPoint.fishPointName,
				fishPoint.fishPointDetailName,
				fishPoint.latitude,
				fishPoint.longitude,
				fishPoint.isBan
			))
			.from(fishPoint)
			.where(withinExpr)
			.fetch();
	}
}

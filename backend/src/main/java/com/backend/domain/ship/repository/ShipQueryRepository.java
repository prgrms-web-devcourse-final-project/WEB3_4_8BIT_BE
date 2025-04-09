package com.backend.domain.ship.repository;

import static com.backend.domain.ship.entity.QShip.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.ship.dto.response.QShipResponse_ShipAll;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Long countByMemberId(final Long memberId) {
		return jpaQueryFactory
			.select(ship.count())
			.from(ship)
			.where(ship.memberId.eq(memberId))
			.fetchOne();
	}
	public List<ShipResponse.ShipAll> findShipAll(Long memberId) {
		return jpaQueryFactory
			.select(new QShipResponse_ShipAll(ship.shipId, ship.shipName, ship.departurePort))
			.from(ship)
			.where(ship.memberId.eq(memberId))
			.fetch();
	}
}

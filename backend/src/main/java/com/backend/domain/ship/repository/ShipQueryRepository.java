package com.backend.domain.ship.repository;

import static com.backend.domain.ship.entity.QShip.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.ship.dto.response.QShipResponse_Detail;
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

	public List<ShipResponse.Detail> findDetailAll(final Long memberId) {
		return jpaQueryFactory
			.select(new QShipResponse_Detail(
				ship.shipId,
				ship.shipName,
				ship.shipNumber,
				ship.departurePort,
				ship.portName,
				ship.restroomType,
				ship.loungeArea,
				ship.kitchenFacility,
				ship.fishingChair,
				ship.passengerInsurance,
				ship.fishingGearRental,
				ship.mealProvided,
				ship.parkingAvailable))
			.from(ship)
			.where(ship.memberId.eq(memberId))
			.fetch();
	}
}

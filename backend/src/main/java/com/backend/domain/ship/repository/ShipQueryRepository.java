package com.backend.domain.ship.repository;

import static com.backend.domain.ship.entity.QShip.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Long countByMemberId(Long memberId) {
		return jpaQueryFactory
			.select(ship.count())
			.from(ship)
			.where(ship.memberId.eq(memberId))
			.fetchOne();
	}
}

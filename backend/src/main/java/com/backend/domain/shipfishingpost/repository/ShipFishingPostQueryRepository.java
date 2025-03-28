package com.backend.domain.shipfishingpost.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

}
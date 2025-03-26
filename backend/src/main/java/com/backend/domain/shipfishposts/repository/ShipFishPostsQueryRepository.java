package com.backend.domain.shipfishposts.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishPostsQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

}

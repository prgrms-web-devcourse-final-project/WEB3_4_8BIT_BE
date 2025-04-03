package com.backend.domain.fishingtrippost.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
}

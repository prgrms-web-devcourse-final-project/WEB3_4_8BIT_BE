package com.backend.domain.fishingtriprecruitment.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripRecruitmentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
}

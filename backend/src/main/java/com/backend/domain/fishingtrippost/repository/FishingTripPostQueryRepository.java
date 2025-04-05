package com.backend.domain.fishingtrippost.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<FishingTripPostResponse.DetailQueryDto> findDetailDtoById(final Long fishingTripPostId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(Projections.constructor(
					FishingTripPostResponse.DetailQueryDto.class,
					fishingTripPost.fishingTripPostId,
					member.name,
					fishingTripPost.subject,
					fishingTripPost.content,
					fishingTripPost.currentCount,
					fishingTripPost.recruitmentCount,
					fishingTripPost.createdAt,
					fishingTripPost.fishingDate,
					fishPoint.fishPointDetailName,
					fishPoint.fishPointName,
					fishPoint.longitude,
					fishPoint.latitude,
					fishingTripPost.fileIdList
				))
				.from(fishingTripPost)
				.leftJoin(member).on(member.memberId.eq(fishingTripPost.memberId))
				.leftJoin(fishPoint).on(fishPoint.fishPointId.eq(fishingTripPost.fishingPointId))
				.where(fishingTripPost.fishingTripPostId.eq(fishingTripPostId))
				.fetchOne()
		);
	}
}

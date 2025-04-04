package com.backend.domain.fishingtrippost.repository;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import com.backend.global.util.StringUtil;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<FishingTripPostResponse.Detail> findDetailById(final Long fishingTripPostId) {

		Tuple tuple = jpaQueryFactory
			.select(
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
			)
			.from(fishingTripPost)
			.leftJoin(member).on(member.memberId.eq(fishingTripPost.memberId))
			.leftJoin(fishPoint).on(fishPoint.fishPointId.eq(fishingTripPost.fishingPointId))
			.where(fishingTripPost.fishingTripPostId.eq(fishingTripPostId))
			.fetchOne();

		if (tuple == null)
			return Optional.empty();
		return
			Optional.of(new FishingTripPostResponse.Detail(
				tuple.get(fishingTripPost.fishingTripPostId),
				tuple.get(member.name),
				tuple.get(fishingTripPost.subject),
				tuple.get(fishingTripPost.content),
				StringUtil.formatParticipantCount(
					tuple.get(fishingTripPost.currentCount),
					tuple.get(fishingTripPost.recruitmentCount)
				),
				StringUtil.formatDate(Objects.requireNonNull(tuple.get(fishingTripPost.createdAt))),
				StringUtil.formatDate(Objects.requireNonNull(tuple.get(fishingTripPost.fishingDate))),
				StringUtil.formatTime(Objects.requireNonNull(tuple.get(fishingTripPost.fishingDate))),
				tuple.get(fishPoint.fishPointDetailName),
				tuple.get(fishPoint.fishPointName),
				tuple.get(fishPoint.longitude),
				tuple.get(fishPoint.latitude),
				tuple.get(fishingTripPost.fileIdList)
			));
	}
}

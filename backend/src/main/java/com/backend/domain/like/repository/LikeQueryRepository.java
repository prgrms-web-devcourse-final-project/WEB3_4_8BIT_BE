package com.backend.domain.like.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.like.entity.QLike.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;

import org.springframework.stereotype.Repository;

import com.backend.domain.like.domain.LikeTargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LikeQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Long countByTargetTypeAndTargetId(final LikeTargetType targetType, final Long targetId) {
		return switch (targetType) {
			case SHIP_FISHING_POST -> jpaQueryFactory
				.select(like.count())
				.from(like)
				.join(shipFishingPost).on(shipFishingPost.shipFishingPostId.eq(like.targetId))
				.where(
					like.targetType.eq(LikeTargetType.SHIP_FISHING_POST),
					like.targetId.eq(targetId),
					like.isDeleted.isFalse()
				)
				.fetchOne();

			case FISHING_TRIP_POST -> jpaQueryFactory
				.select(like.count())
				.from(like)
				.join(fishingTripPost).on(fishingTripPost.fishingTripPostId.eq(like.targetId))
				.where(
					like.targetType.eq(LikeTargetType.FISHING_TRIP_POST),
					like.targetId.eq(targetId),
					like.isDeleted.isFalse()
				)
				.fetchOne();
		};
	}

	public void deleteByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		jpaQueryFactory.update(like)
			.set(like.isDeleted, true)
			.where(
				like.memberId.eq(memberId),
				like.targetType.eq(targetType),
				like.targetId.eq(targetId),
				like.isDeleted.isFalse()
			)
			.execute();
	}

	public void restoreByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		jpaQueryFactory.update(like)
			.set(like.isDeleted, false)
			.where(
				like.memberId.eq(memberId),
				like.targetType.eq(targetType),
				like.targetId.eq(targetId),
				like.isDeleted.isTrue()
			)
			.execute();
	}

	/**
	 * isDeleted = true 좋아요 영구 삭제
	 *
	 * @return 삭제된 row 수
	 */
	public int deleteAllSoftDeletedLikes() {
		return (int)jpaQueryFactory.delete(like)
			.where(like.isDeleted.isTrue())
			.execute();
	}
}

package com.backend.domain.like.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.like.entity.QLike.*;
import static com.backend.domain.region.entity.QRegion.*;
import static com.backend.domain.review.entity.QReview.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.dto.response.QLikeResponse_FishingTripPostLikedQueryDto;
import com.backend.domain.like.dto.response.QLikeResponse_ShipFishingPostLikedQueryDto;
import com.backend.domain.like.entity.QLike;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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

	public int deleteAllSoftDeletedLikes() {
		return (int)jpaQueryFactory.delete(like)
			.where(like.isDeleted.isTrue())
			.execute();
	}

	public List<LikeResponse.FishingTripPostLikedQueryDto> getLikedFishingTripPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		return jpaQueryFactory
			.select(new QLikeResponse_FishingTripPostLikedQueryDto(
				fishingTripPost.fishingTripPostId,
				fishingTripPost.regionId,
				region.type,
				fishingTripPost.subject,
				fishingTripPost.content,
				fishingTripPost.fishingDate,
				like.createdAt, // 좋아요 누른 시각
				fishingTripPost.recruitmentCount,
				fishingTripPost.postStatus,
				fishingTripPost.fileIdList,
				fishingTripPost.commentCount,
				fishingTripPost.likeCount
			))
			.from(fishingTripPost)
			.leftJoin(like).on(
				like.targetId.eq(fishingTripPost.fishingTripPostId)
					.and(like.targetType.eq(LikeTargetType.FISHING_TRIP_POST))
					.and(like.memberId.eq(memberId))
					.and(like.isDeleted.isFalse())
			)
			.leftJoin(region).on(fishingTripPost.regionId.eq(region.regionId))
			.where(cursorCondition(cursorRequestDto))
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();
	}

	public List<LikeResponse.ShipFishingPostLikedQueryDto> getLikedShipFishingPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		return jpaQueryFactory
			.select(new QLikeResponse_ShipFishingPostLikedQueryDto(
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.location,
				shipFishingPost.price,
				shipFishingPost.fileIdList,
				shipFishingPost.fishIdList,
				shipFishingPost.reviewEverRate,
				like.createdAt,
				shipFishingPost.likeCount,
				//TODO 추후 필드값에 추가시 변경 필요 (선상 낚시 리뷰 수)
				JPAExpressions
					.select(review.count())
					.from(review)
					.where(review.shipFishingPostId.eq(shipFishingPost.shipFishingPostId))
			))
			.from(shipFishingPost)
			.leftJoin(like).on(
				like.targetType.eq(LikeTargetType.SHIP_FISHING_POST),
				like.targetId.eq(shipFishingPost.shipFishingPostId),
				like.memberId.eq(memberId),
				like.isDeleted.isFalse()
			)
			.where(cursorCondition(cursorRequestDto))
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();
	}

	private BooleanExpression cursorCondition(final GlobalRequest.CursorRequest cursorRequestDto) {
		if (cursorRequestDto.fieldValue() == null || cursorRequestDto.id() == null) {
			return null;
		}
		return getBooleanExpressionByCreatedAt(cursorRequestDto);
	}

	/**
	 * 커서 페이징을 위한 where 조건 - 좋아요 최신순에 맞게 구성
	 *
	 * @param cursorRequestDto 커서 정보
	 * @return BooleanExpression
	 */
	private static BooleanExpression getBooleanExpressionByCreatedAt(
		final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ZonedDateTime value = ZonedDateTime.parse(cursorRequestDto.fieldValue());
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return QuerydslUtil.createFieldPredicate(
			like.likeId,
			cursorRequestDto.id(),
			like.createdAt,
			value,
			order
		);
	}

	/**
	 * 좋아요 누른 게시글을 최신순으로 조회하는 쿼리용 정렬 메서드
	 *
	 * @param cursorRequestDto 커서 요청 정보
	 * @return OrderBy 조건 (like.createdAt desc, like.id desc)
	 */
	private static OrderSpecifier<?>[] getOrderBy(final GlobalRequest.CursorRequest cursorRequestDto) {
		Order direction = QuerydslUtil.getOrder(cursorRequestDto);

		return new OrderSpecifier[] {
			new OrderSpecifier<>(direction, QLike.like.createdAt),
			new OrderSpecifier<>(direction, QLike.like.likeId)
		};
	}
}

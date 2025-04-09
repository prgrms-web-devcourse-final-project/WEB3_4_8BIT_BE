package com.backend.domain.fishingtrippost.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.region.entity.QRegion.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.dto.response.QFishingTripPostResponse_DetailPageQueryDto;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	//TODO 인기순 나중에 추가해야함
	Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
		"createdAt", fishingTripPost.createdAt
	);

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
					fishingTripPost.fileIdList,
					fishingTripPost.postStatus
				))
				.from(fishingTripPost)
				.leftJoin(member).on(member.memberId.eq(fishingTripPost.memberId))
				.leftJoin(fishPoint).on(fishPoint.fishPointId.eq(fishingTripPost.fishingPointId))
				.where(fishingTripPost.fishingTripPostId.eq(fishingTripPostId))
				.fetchOne()
		);
	}

	public List<FishingTripPostResponse.DetailPageQueryDto> findScrollDetailPageDto(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword
	) {
		return jpaQueryFactory
			.select(new QFishingTripPostResponse_DetailPageQueryDto(
				fishingTripPost.fishingTripPostId,
				fishingTripPost.regionId,
				region.type,
				fishingTripPost.subject,
				fishingTripPost.content,
				fishingTripPost.fishingDate,
				fishingTripPost.createdAt,
				fishingTripPost.recruitmentCount,
				fishingTripPost.postStatus,
				fishingTripPost.fileIdList
			))
			.from(fishingTripPost)
			.leftJoin(region).on(fishingTripPost.regionId.eq(region.regionId))
			.where(
				whereCondition(regionId, status, keyword),
				cursorCondition(cursorRequestDto)
			)
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();
	}

	public void updateLikeCount(Long postId, Long likeCount) {
		jpaQueryFactory.update(fishingTripPost)
			.set(fishingTripPost.likeCount, likeCount)
			.where(fishingTripPost.fishingTripPostId.eq(postId))
			.execute();
	}

	private BooleanExpression whereCondition(final Long regionId,
		final PostStatus status,
		final String keyword) {
		BooleanExpression condition = Expressions.TRUE;

		if (regionId != null) {
			condition = condition.and(region.regionId.eq(regionId));
		}

		if (status != null) {
			condition = condition.and(fishingTripPost.postStatus.eq(status));
		}

		if (StringUtils.hasText(keyword)) {
			condition = condition.and(fishingTripPost.subject.containsIgnoreCase(keyword));
		}

		return condition;
	}

	private BooleanExpression cursorCondition(final GlobalRequest.CursorRequest cursorRequestDto) {
		if (cursorRequestDto.fieldValue() == null || cursorRequestDto.id() == null)
			return null;
		// TODO: 다른 정렬 필드 추가 시 확장
		return switch (cursorRequestDto.sort()) {
			case "createdAt" -> getBooleanExpressionByCreatedAt(cursorRequestDto);

			default -> throw new IllegalArgumentException("지원되지 않는 정렬 필드: " + cursorRequestDto.sort());
		};
	}

	private static BooleanExpression getBooleanExpressionByCreatedAt(
		final GlobalRequest.CursorRequest cursorRequestDto) {
		ZonedDateTime value = ZonedDateTime.parse(cursorRequestDto.fieldValue());
		boolean isNext = "next".equalsIgnoreCase(cursorRequestDto.type());
		boolean isAsc = "asc".equalsIgnoreCase(cursorRequestDto.order());

		if ((isAsc && isNext) || (!isAsc && !isNext)) {
			return fishingTripPost.createdAt.gt(value)
				.or(fishingTripPost.createdAt.eq(value)
					.and(fishingTripPost.fishingTripPostId.gt(cursorRequestDto.id())));
		} else {
			return fishingTripPost.createdAt.lt(value)
				.or(fishingTripPost.createdAt.eq(value)
					.and(fishingTripPost.fishingTripPostId.lt(cursorRequestDto.id())));
		}
	}

	private OrderSpecifier<?>[] getOrderBy(final GlobalRequest.CursorRequest cursorRequestDto) {
		Order direction = QuerydslUtil.getOrder(cursorRequestDto);

		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(direction, fishingTripPost.createdAt),
			new OrderSpecifier<>(direction, fishingTripPost.fishingTripPostId)
		};
	}
}

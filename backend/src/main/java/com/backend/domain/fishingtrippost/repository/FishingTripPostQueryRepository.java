package com.backend.domain.fishingtrippost.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.fishingtriprecruitment.entity.QFishingTripRecruitment.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.region.entity.QRegion.*;
import static com.backend.global.storage.entity.QFile.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;

import com.backend.domain.fishingtrippost.dto.response.QFishingTripPostResponse_DetailPageQueryDto;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
					fishingTripPost.fileIdList,
					fishingTripPost.postStatus,
					fishingTripPost.likeCount
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

	public boolean updateLikeCount(final Long postId, final Long likeCount) {
		return jpaQueryFactory.update(fishingTripPost)
			.set(fishingTripPost.likeCount, likeCount)
			.where(fishingTripPost.fishingTripPostId.eq(postId))
			.execute() > 0;
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
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return QuerydslUtil.createFieldPredicate(
			fishingTripPost.fishingTripPostId, cursorRequestDto.id(),
			fishingTripPost.createdAt, value,
			order
		);
	}

	private OrderSpecifier<?>[] getOrderBy(final GlobalRequest.CursorRequest cursorRequestDto) {
		Order direction = QuerydslUtil.getOrder(cursorRequestDto);

		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(direction, fishingTripPost.createdAt),
			new OrderSpecifier<>(direction, fishingTripPost.fishingTripPostId)
		};
	}

	public FishingTripPostResponse.ParticipantDetailDto findParticipantDetailDto(
		final Long fishingTripPostId,
		final Long memberId) {
		return jpaQueryFactory
			.select(Projections.constructor(
				FishingTripPostResponse.ParticipantDetailDto.class,
				fishingTripPost.fishingTripPostId,
				fishingTripPost.recruitmentCount,
				fishingTripPost.currentCount,
				fishingTripPost.postStatus,
				Expressions.asBoolean(isParticipant(memberId)),
				Expressions.asBoolean(isWriterExpression(memberId)),
				member.memberId,
				member.nickname,
				file.url
			))
			.from(fishingTripPost)
			.leftJoin(member).on(fishingTripPost.memberId.eq(member.memberId))
			.leftJoin(file).on(member.fileId.eq(file.fileId))
			.where(fishingTripPost.fishingTripPostId.eq(fishingTripPostId))
			.fetchOne();
	}

	private static BooleanExpression isWriterExpression(final Long memberId) {
		if (memberId == null) {
			return Expressions.FALSE.isTrue();
		}
		return fishingTripPost.memberId.eq(memberId);
	}

	private static BooleanExpression isParticipant(final Long memberId) {
		if (memberId == null) {
			return Expressions.FALSE.isTrue(); // 로그인 안 했으면 false
		}
		return JPAExpressions
			.selectOne()
			.from(fishingTripRecruitment)
			.where(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPost.fishingTripPostId)
				.and(fishingTripRecruitment.memberId.eq(memberId))
				.and(fishingTripRecruitment.recruitmentStatus.eq(RecruitmentStatus.APPROVED)))
			.exists();
	}

	public List<FishingTripPostResponse.ParticipantDetail> findApprovedParticipants(
		final Long fishingTripPostId) {
		return jpaQueryFactory
			.select(Projections.constructor(
				FishingTripPostResponse.ParticipantDetail.class,
				member.memberId,
				member.nickname,
				file.url
			))
			.from(fishingTripRecruitment)
			.join(member).on(fishingTripRecruitment.memberId.eq(member.memberId))
			.leftJoin(file).on(member.fileId.eq(file.fileId))
			.where(
				fishingTripRecruitment.fishingTripPostId.eq(fishingTripPostId),
				fishingTripRecruitment.recruitmentStatus.eq(RecruitmentStatus.APPROVED)
			)
			.fetch();
	}

	public ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> findMyFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus postStatus,
		final Long memberId
	) {
		List<FishingTripPostResponse.MyFishingTripPostDetailPage> myFishingTripPostDetailPages = jpaQueryFactory.selectDistinct(
				Projections.constructor(
					FishingTripPostResponse.MyFishingTripPostDetailPage.class,
					fishingTripPost.fishingTripPostId,
					fishingTripPost.subject,
					fishingTripPost.fishingPointId,
					fishPoint.fishPointName,
					fishPoint.fishPointDetailName,
					fishingTripPost.fishingDate,
					fishingTripPost.createdAt,
					fishingTripPost.currentCount,
					fishingTripPost.recruitmentCount,
					fishingTripPost.postStatus,
					fishingTripPost.commentCount,
					fishingTripPost.likeCount
				))
			.from(fishingTripRecruitment)
			.innerJoin(fishingTripPost)
			.on(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPost.fishingTripPostId))
			.leftJoin(fishPoint)
			.on(fishPoint.fishPointId.eq(fishingTripPost.fishingPointId))
			.where(fishingTripRecruitment.memberId.eq(memberId),
				fishingTripPost.postStatus.eq(postStatus),
				cursorCondition(cursorRequestDto))
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean isLast = myFishingTripPostDetailPages.size() <= cursorRequestDto.size();

		if (!isLast)
			myFishingTripPostDetailPages.remove(myFishingTripPostDetailPages.size() - 1);

		return ScrollResponse.from(
			myFishingTripPostDetailPages,
			cursorRequestDto.size(),
			myFishingTripPostDetailPages.size(),
			cursorRequestDto.fieldValue() == null,
			isLast
		);
	}
}

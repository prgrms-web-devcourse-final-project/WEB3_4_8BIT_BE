package com.backend.domain.review.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.review.entity.QReview.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByPostId(
		final Long postId,
		final Pageable pageable
	) {
		List<ReviewWithMemberResponse> contents = jpaQueryFactory
			.select(Projections.constructor(
				ReviewWithMemberResponse.class,
				review.reviewId,
				review.rating,
				review.content,
				review.fileIdList,
				review.shipFishingPostId,
				review.memberId,
				member.nickname,
				file.url,
				review.createdAt
			))
			.from(review)
			.join(member).on(review.memberId.eq(member.memberId))
			.leftJoin(file).on(member.fileId.eq(file.fileId))
			.where(review.shipFishingPostId.eq(postId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(contents, pageable);
	}

	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(
		final Long memberId,
		final Pageable pageable
	) {
		List<ReviewWithMemberResponse> contents = jpaQueryFactory
			.select(Projections.constructor(
				ReviewWithMemberResponse.class,
				review.reviewId,
				review.rating,
				review.content,
				review.fileIdList,
				review.shipFishingPostId,
				review.memberId,
				member.nickname,
				file.url,
				review.createdAt
			))
			.from(review)
			.join(member).on(review.memberId.eq(member.memberId))
			.leftJoin(file).on(member.fileId.eq(file.fileId))
			.where(review.memberId.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(contents, pageable);
	}

	private Slice<ReviewWithMemberResponse> checkLastPage(List<ReviewWithMemberResponse> contents, Pageable pageable) {
		boolean hasNext = false;
		if (contents.size() > pageable.getPageSize()) {
			contents.remove(pageable.getPageSize());
			hasNext = true;
		}
		return new SliceImpl<>(contents, pageable, hasNext);
	}

}

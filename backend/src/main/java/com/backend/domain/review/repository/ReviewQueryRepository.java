package com.backend.domain.review.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.review.entity.QReview.*;
import static com.backend.global.storage.entity.QFile.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	// Offset 방식 - 게시글 기준
	public Slice<ReviewWithMemberResponse> findReviewsByPostId(final Long postId, final Pageable pageable) {
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(review.shipFishingPostId.eq(postId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return getReviewWithMemberResponses(pageable, reviews);
	}

	// Offset 방식 - 작성자 기준
	public Slice<ReviewWithMemberResponse> findReviewsByMemberId(final Long memberId, final Pageable pageable) {
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(review.memberId.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return getReviewWithMemberResponses(pageable, reviews);
	}

	private Slice<ReviewWithMemberResponse> getReviewWithMemberResponses(final Pageable pageable, List<Review> reviews) {
		boolean hasNext = reviews.size() > pageable.getPageSize();
		if (hasNext) {
			reviews = reviews.subList(0, pageable.getPageSize());
		}

		List<ReviewWithMemberResponse> content = mapToDto(reviews);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	// Cursor 방식 - 게시글 기준
	public ScrollResponse<ReviewWithMemberResponse> findReviewsByPostIdWithCursor(
		final Long postId,
		final GlobalRequest.CursorRequest cursor
	) {
		int limit = cursor.size();
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(
				review.shipFishingPostId.eq(postId),
				cursorCondition(cursor)
			)
			.orderBy(review.createdAt.desc(), review.reviewId.desc())
			.limit(limit + 1)
			.fetch();

		return getReviewWithMemberResponseScrollResponse(cursor, limit, reviews);
	}

	// Cursor 방식 - 작성자 기준
	public ScrollResponse<ReviewWithMemberResponse> findReviewsByMemberIdWithCursor(
		final Long memberId,
		final GlobalRequest.CursorRequest cursor
	) {
		int limit = cursor.size();
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(
				review.memberId.eq(memberId),
				cursorCondition(cursor)
			)
			.orderBy(review.createdAt.desc(), review.reviewId.desc())
			.limit(limit + 1)
			.fetch();

		return getReviewWithMemberResponseScrollResponse(cursor, limit, reviews);
	}

	private ScrollResponse<ReviewWithMemberResponse> getReviewWithMemberResponseScrollResponse(
		final GlobalRequest.CursorRequest cursor,
		final int limit,
		List<Review> reviews
	) {
		boolean hasNext = reviews.size() > limit;
		if (hasNext) {
			reviews = reviews.subList(0, limit);
		}

		List<ReviewWithMemberResponse> content = mapToDto(reviews);
		return ScrollResponse.from(content, limit, content.size(), cursor.fieldValue() == null, !hasNext);
	}

	// 커서 조건 생성
	private BooleanExpression cursorCondition(final GlobalRequest.CursorRequest cursor) {
		if (cursor.fieldValue() == null || cursor.id() == null) return null;

		ZonedDateTime fieldTime = ZonedDateTime.parse(cursor.fieldValue());

		return review.createdAt.lt(fieldTime)
			.or(review.createdAt.eq(fieldTime).and(review.reviewId.lt(cursor.id())));
	}

	// 공통 DTO 변환
	private List<ReviewWithMemberResponse> mapToDto(final List<Review> reviews) {
		Set<Long> allFileIds = reviews.stream()
			.flatMap(r -> r.getFileIdList().stream())
			.collect(Collectors.toSet());

		Map<Long, String> fileUrlMap = jpaQueryFactory
			.select(file.fileId, file.url)
			.from(file)
			.where(file.fileId.in(allFileIds))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				t -> t.get(file.fileId),
				t -> t.get(file.url)
			));

		Map<Long, Tuple> memberMap = jpaQueryFactory
			.select(member.memberId, member.nickname, file.url)
			.from(member)
			.leftJoin(file).on(member.fileId.eq(file.fileId))
			.where(member.memberId.in(reviews.stream()
				.map(Review::getMemberId)
				.collect(Collectors.toSet())))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				t -> t.get(member.memberId),
				t -> t
			));

		return reviews.stream()
			.map(r -> {
				Tuple m = memberMap.get(r.getMemberId());
				String nickname = m != null ? m.get(member.nickname) : null;
				String profileImg = m != null ? m.get(file.url) : null;

				List<String> fileUrlList = r.getFileIdList().stream()
					.map(fileUrlMap::get)
					.filter(Objects::nonNull)
					.toList();

				return new ReviewWithMemberResponse(
					r.getReviewId(),
					r.getRating(),
					r.getContent(),
					fileUrlList,
					r.getShipFishingPostId(),
					r.getMemberId(),
					nickname,
					profileImg,
					r.getCreatedAt()
				);
			})
			.toList();
	}
}

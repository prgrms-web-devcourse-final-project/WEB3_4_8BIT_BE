package com.backend.domain.review.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.review.entity.QReview.*;
import static com.backend.global.storage.entity.QFile.*;

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
import com.querydsl.core.Tuple;
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
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(review.shipFishingPostId.eq(postId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return mapToDtoSlice(reviews, pageable);
	}

	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(
		final Long memberId,
		final Pageable pageable
	) {
		List<Review> reviews = jpaQueryFactory
			.selectFrom(review)
			.where(review.memberId.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return mapToDtoSlice(reviews, pageable);
	}

	private Slice<ReviewWithMemberResponse> mapToDtoSlice(List<Review> reviews, Pageable pageable) {
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

		List<ReviewWithMemberResponse> content = reviews.stream()
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

		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content = content.subList(0, pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}
}

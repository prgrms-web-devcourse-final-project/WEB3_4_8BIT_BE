package com.backend.domain.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

	boolean existsByReservationId(final Long reservationId);

	@Query(
		"""
		SELECT new com.backend.domain.review.dto.response.ReviewWithMemberResponse(
			r.reviewId,
			r.rating,
			r.content,
			r.fileIdList,
			r.shipFishingPostId,
			r.memberId,
			m.nickname,
			f.url,
			r.createdAt
		)
		FROM Review r
		JOIN Member m ON r.memberId = m.memberId
		LEFT JOIN File f ON m.fileId = f.fileId
		WHERE r.shipFishingPostId = :postId
		""")
	Slice<ReviewWithMemberResponse> findReviewsWithMemberByPostId(@Param("postId") final Long postId, final Pageable pageable);

	@Query(
		"""
		SELECT new com.backend.domain.review.dto.response.ReviewWithMemberResponse(
			r.reviewId,
			r.rating,
			r.content,
			r.fileIdList,
			r.shipFishingPostId,
			r.memberId,
			m.nickname,
			f.url,
			r.createdAt
		)
		FROM Review r
		JOIN Member m ON r.memberId = m.memberId
		LEFT JOIN File f ON m.fileId = f.fileId
		WHERE r.memberId = :memberId
		"""
	)
	Slice<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(@Param("memberId") final Long memberId, final Pageable pageable);
}

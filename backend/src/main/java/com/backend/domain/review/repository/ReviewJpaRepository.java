package com.backend.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
			r.imageList,
			r.shipFishingPostId,
			r.memberId,
			m.nickname,
			m.profileImg,
			r.createdAt
		)
		FROM Review r
		JOIN Member m ON r.memberId = m.memberId
		WHERE r.shipFishingPostId = :postId
		""")
	Page<ReviewWithMemberResponse> findReviewsWithMemberByPostId(@Param("postId") final Long postId, final Pageable pageable);

	@Query(
		"""
		SELECT new com.backend.domain.review.dto.response.ReviewWithMemberResponse(
			r.reviewId,
			r.rating,
			r.content,
			r.imageList,
			r.shipFishingPostId,
			r.memberId,
			m.nickname,
			m.profileImg,
			r.createdAt
		)
		FROM Review r
		JOIN Member m ON r.memberId = m.memberId
		WHERE r.memberId = :memberId
		"""
	)
	Page<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(@Param("memberId") final Long memberId, final Pageable pageable);
}

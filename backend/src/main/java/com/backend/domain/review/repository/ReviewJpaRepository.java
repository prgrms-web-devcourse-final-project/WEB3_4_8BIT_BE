package com.backend.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

	boolean existsByReservationId(Long reservationId);

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
	List<ReviewWithMemberResponse> findReviewsWithMemberByPostId(@Param("postId") Long postId);

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
	List<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(@Param("memberId") Long memberId);
}

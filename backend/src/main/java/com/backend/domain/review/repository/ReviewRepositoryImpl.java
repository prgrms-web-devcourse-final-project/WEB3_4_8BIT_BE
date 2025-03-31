package com.backend.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;

	@Override
	public Review save(Review review) {
		return reviewJpaRepository.save(review);
	}

	@Override
	public boolean existsByReservationId(Long reservationId) {
		return reviewJpaRepository.existsByReservationId(reservationId);
	}

	@Override
	public Page<ReviewWithMemberResponse> findReviewsWithMemberByPostId(Long postId, Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByPostId(postId, pageable);
	}

	@Override
	public Page<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(Long memberId, Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByMemberId(memberId, pageable);
	}
}

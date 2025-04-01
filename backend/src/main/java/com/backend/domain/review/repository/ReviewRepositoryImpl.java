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
	public Review save(final Review review) {
		return reviewJpaRepository.save(review);
	}

	@Override
	public boolean existsByReservationId(final Long reservationId) {
		return reviewJpaRepository.existsByReservationId(reservationId);
	}

	@Override
	public Page<ReviewWithMemberResponse> findReviewsWithMemberByPostId(final Long postId, final Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByPostId(postId, pageable);
	}

	@Override
	public Page<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(final Long memberId, final Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByMemberId(memberId, pageable);
	}
}

package com.backend.domain.review.repository;

import java.util.List;

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
	public List<ReviewWithMemberResponse> findReviewsWithMemberByPostId(Long postId) {
		return reviewJpaRepository.findReviewsWithMemberByPostId(postId);
	}

	@Override
	public List<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(Long memberId) {
		return reviewJpaRepository.findReviewsWithMemberByMemberId(memberId);
	}
}

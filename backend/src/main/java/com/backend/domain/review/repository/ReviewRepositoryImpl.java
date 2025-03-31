package com.backend.domain.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.Optional;

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
	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByPostId(final Long postId, final Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByPostId(postId, pageable);
	}

	@Override
	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(final Long memberId, final Pageable pageable) {
		return reviewJpaRepository.findReviewsWithMemberByMemberId(memberId, pageable);
	}

	@Override
	public Optional<Review> findById(Long id) {
		return reviewJpaRepository.findById(id);
	}

	@Override
	public void delete(Review review) {
		reviewJpaRepository.delete(review);
	}
}

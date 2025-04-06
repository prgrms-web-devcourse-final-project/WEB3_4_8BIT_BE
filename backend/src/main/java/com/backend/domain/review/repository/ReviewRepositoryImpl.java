package com.backend.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;
	private final ReviewQueryRepository reviewQueryRepository;


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
		return reviewQueryRepository.findReviewsByPostId(postId, pageable);
	}

	@Override
	public Slice<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(final Long memberId, final Pageable pageable) {
		return reviewQueryRepository.findReviewsByMemberId(memberId, pageable);
	}

	@Override
	public ScrollResponse<ReviewWithMemberResponse> findReviewsByPostIdWithCursor(
		final Long postId,
		final GlobalRequest.CursorRequest cursorRequestDto
	) {
		return reviewQueryRepository.findReviewsByPostIdWithCursor(postId, cursorRequestDto);
	}

	@Override
	public ScrollResponse<ReviewWithMemberResponse> findReviewsByMemberIdWithCursor(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto
	) {
		return reviewQueryRepository.findReviewsByMemberIdWithCursor(memberId, cursorRequestDto);
	}

	@Override
	public Optional<Review> findById(final Long id) {
		return reviewJpaRepository.findById(id);
	}

	@Override
	public void delete(final Review review) {
		reviewJpaRepository.delete(review);
	}
}

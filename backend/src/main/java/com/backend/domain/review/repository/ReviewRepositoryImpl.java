package com.backend.domain.review.repository;

import org.springframework.stereotype.Repository;

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
		return reviewJpaRepository.existsById(reservationId);
	}
}

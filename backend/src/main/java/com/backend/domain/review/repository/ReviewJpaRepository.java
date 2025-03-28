package com.backend.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

	boolean existsByReservationId(Long reservationId);
}

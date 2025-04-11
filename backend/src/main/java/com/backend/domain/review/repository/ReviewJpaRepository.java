package com.backend.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.review.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

	boolean existsByReservationId(final Long reservationId);

	List<Review> findAllByShipFishingPostId(final Long shipFishingPostId);
}

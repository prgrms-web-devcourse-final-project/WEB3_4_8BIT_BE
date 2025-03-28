package com.backend.domain.review.converter;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Review;

import lombok.Builder;

@Builder
public class ReviewConverter {

	public static Review fromReviewRequestCreate(Long reservationId, ReviewRequest.Create request) {

		return Review.builder()
			.reservationId(reservationId)
			.rating(request.rating())
			.content(request.content())
			.images(request.images())
			.memberId(request.memberId())
			.shipFishingPostId(request.shipFishingPostId())
			.build();
	}
}

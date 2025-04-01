package com.backend.domain.review.converter;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Review;

import lombok.Builder;

@Builder
public class ReviewConverter {

	public static Review fromReviewRequestCreate(Long memberId, Long reservationId, ReviewRequest.Create request) {

		return Review.builder()
			.reservationId(reservationId)
			.rating(request.rating())
			.content(request.content())
			.imageList(request.imageList())
			.memberId(memberId)
			.shipFishingPostId(request.shipFishingPostId())
			.build();
	}
}

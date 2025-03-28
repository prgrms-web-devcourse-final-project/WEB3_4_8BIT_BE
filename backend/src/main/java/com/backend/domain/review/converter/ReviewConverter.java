package com.backend.domain.review.converter;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Reviews;

import lombok.Builder;

@Builder
public class ReviewConverter {

	public static Reviews fromReviewRequestCreate(Long shipFishPostId, ReviewRequest.Create request) {

		return Reviews.builder()
			.rating(request.rating())
			.content(request.content())
			.images(request.images())
			.memberId(request.memberId())
			.shipFishPostId(shipFishPostId)
			.build();
	}
}

package com.backend.domain.review.service;

import com.backend.domain.review.dto.request.ReviewRequest;

public interface ReviewService {

	/**
	 * 선상 낚시 리뷰 생성
	 *
	 * @param reservationId 선상낚시 예약 ID
	 * @param request 리뷰 작성 요청 DTO
	 * @return 생성된 리뷰 ID
	 */
	Long save(Long reservationId, ReviewRequest.CreateReview request);
}
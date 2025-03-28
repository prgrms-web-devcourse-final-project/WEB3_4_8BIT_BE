package com.backend.domain.review.service;

import com.backend.domain.review.dto.request.ReviewRequest;

public interface ReviewService {

	/**
	 * 선상 낚시 리뷰 생성
	 *
	 * @param shipFishPostId 선상낚시 게시글 ID
	 * @param request 리뷰 작성 요청 DTO
	 * @return 생성된 리뷰 ID
	 */
	Long createReview(Long shipFishPostId, ReviewRequest.Create request);
}
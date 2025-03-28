package com.backend.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Reviews;
import com.backend.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// TODO 리뷰 작성 후 리뷰 상세 페이지로 이동하는지, 아니면 게시글 상세보기로 이동하는지 확인 필요, 추후 수정
	@Transactional
	public Long createReview(Long shipFishPostId, ReviewRequest.Create request) {
		Reviews review = ReviewConverter.fromReviewRequestCreate(shipFishPostId, request);
		return reviewRepository.save(review).getReviewId();
	}

}

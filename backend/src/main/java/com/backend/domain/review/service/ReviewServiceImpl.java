package com.backend.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Reviews;
import com.backend.domain.review.exception.ReviewErrorCode;
import com.backend.domain.review.exception.ReviewException;
import com.backend.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	// TODO 리뷰 작성 후 리뷰 상세 페이지로 이동하는지, 아니면 게시글 상세보기로 이동하는지 확인 필요, 추후 수정
	@Override
	@Transactional
	public Long createReview(Long reservationId, ReviewRequest.Create request) {

		// 리뷰 중복 검증 TODO 게시글 id 검증은 선상 낚시 게시글 기능 구현 이후에 추가
		if(reviewRepository.existsByMemberIdAndShipFishPostId(request.memberId(), reservationId)) {
			throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
		}

		Reviews review = ReviewConverter.fromReviewRequestCreate(reservationId, request);
		return reviewRepository.save(review).getReviewId();
	}
}

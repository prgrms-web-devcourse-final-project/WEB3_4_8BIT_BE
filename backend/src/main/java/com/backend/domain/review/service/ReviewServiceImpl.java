package com.backend.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Review;
import com.backend.domain.review.exception.ReviewErrorCode;
import com.backend.domain.review.exception.ReviewException;
import com.backend.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	@Override
	@Transactional
	public Long save(final Long memberId, final Long reservationId, final ReviewRequest.Create request) {

		// 리뷰 중복 검증 TODO 예약 검증은 선상 낚시 예약 기능 구현 이후에 추가
		if(reviewRepository.existsByReservationId(reservationId)) {
			throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
		}

		Review review = ReviewConverter.fromReviewRequestCreate(memberId, reservationId, request);
		Review savedReview = reviewRepository.save(review);

		log.debug("선상 낚시 리뷰 저장: {}", savedReview);
		return savedReview.getReviewId();
	}
}

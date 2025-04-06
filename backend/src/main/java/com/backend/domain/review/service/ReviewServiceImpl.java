package com.backend.domain.review.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;
import com.backend.domain.review.exception.ReviewErrorCode;
import com.backend.domain.review.exception.ReviewException;
import com.backend.domain.review.repository.ReviewRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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

		// 리뷰 중복 검증
		validateDuplicate(reservationId);

		// TODO 예약 검증은 선상 낚시 예약 기능 구현 이후에 추가

		Review review = ReviewConverter.fromReviewRequestCreate(memberId, reservationId, request);
		Review savedReview = reviewRepository.save(review);

		log.debug("[리뷰 저장] 저장된 리뷰: {}", savedReview);
		return savedReview.getReviewId();
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<ReviewWithMemberResponse> getReviewListByPostId(final Long memberId, final Long postId, final Pageable pageable) {

		Slice<ReviewWithMemberResponse> reviewList = reviewRepository.findReviewsWithMemberByPostId(memberId, postId, pageable);

		logReviewList("게시글", postId, reviewList);
		return reviewList;
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<ReviewWithMemberResponse> getReviewListByMemberId(final Long memberId, final Pageable pageable) {

		Slice<ReviewWithMemberResponse> reviewList = reviewRepository.findReviewsWithMemberByMemberId(memberId, pageable);

		logReviewList("회원", memberId, reviewList);
		return reviewList;
	}

	@Override
	public ScrollResponse<ReviewWithMemberResponse> getReviewListByPostIdWithCursor(
		final Long postId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ScrollResponse<ReviewWithMemberResponse> reviewList = reviewRepository.findReviewsByPostIdWithCursor(
			postId, memberId, cursorRequestDto);

		logReviewList("게시글", postId, reviewList);
		return reviewList;
	}

	@Override
	public ScrollResponse<ReviewWithMemberResponse> getReviewListByMemberIdWithCursor(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ScrollResponse<ReviewWithMemberResponse> reviewList = reviewRepository.findReviewsByMemberIdWithCursor(
			memberId, cursorRequestDto);

		logReviewList("회원", memberId, reviewList);
		return reviewList;
	}

	@Override
	@Transactional
	public void delete(final Long memberId, final Long reviewId) {
		//리뷰 조회
		Review review = getReviewById(reviewId);

		//리뷰 삭제 권한 검증
		validateReviewOwner(review, memberId);

		reviewRepository.delete(review);
		log.debug("리뷰가 삭제되었습니다. reviewId={}, 삭제한 회원 ID={}", reviewId, memberId);
	}

	private Review getReviewById(final Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_FOUND_REVIEW));
	}

	private void validateReviewOwner(final Review review, final Long memberId) {
		if (!review.getMemberId().equals(memberId)) {
			throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW_DELETE);
		}
	}

	private void validateDuplicate(final Long reservationId) {
		if(reviewRepository.existsByReservationId(reservationId)) {
			throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
		}
	}

	private void logReviewList(final String targetType, final Long targetId, final Object result) {
		log.debug("[리뷰 조회] {} ID {}의 리뷰 목록: {}", targetType, targetId, result);
	}
}

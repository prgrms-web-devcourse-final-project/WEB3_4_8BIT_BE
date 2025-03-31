package com.backend.domain.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;

public interface ReviewService {

	/**
	 * 선상 낚시 리뷰 생성
	 *
	 * @param memberId 회원 ID
	 * @param reservationId 선상낚시 예약 ID
	 * @param request 리뷰 작성 요청 DTO
	 * @return 생성된 리뷰 ID
	 */
	Long save(Long memberId, Long reservationId, ReviewRequest.Create request);

	/**
	 * 선상 낚시 리뷰 조회
	 *
	 * @param postId 게시글 ID
	 * @return {@link Page<ReviewWithMemberResponse>}
	 */
	Page<ReviewWithMemberResponse> getReviewListByPostId(Long postId, Pageable pageable);

	/**
	 * 내가 작성한 리뷰 조회
	 *
	 * @param memberId	회원 ID
	 * @return {@link Page<ReviewWithMemberResponse>}
	 */
	Page<ReviewWithMemberResponse> getReviewListByMemberId(Long memberId, Pageable pageable);
}
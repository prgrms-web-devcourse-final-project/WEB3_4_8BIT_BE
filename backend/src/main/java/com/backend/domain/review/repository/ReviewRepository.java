package com.backend.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;

public interface ReviewRepository {

	/**
	 * 리뷰 저장 메서드
	 *
	 * @param review {@link Review}
	 * @return {@link Review}
	 * @implSpec 선상 낚시에 대한 리뷰 저장 메서드
	 */
	Review save(final Review review);

	/**
	 * 예약에 리뷰가 있는지 검증 하는 메서드
	 *
	 * @param reservationId 예약 ID
	 * @return 예약에 리뷰가 존재하면 true, 없으면 false
	 * @implSpec 예약 ID를 기반으로 해당 예약에 대한 리뷰 존재 여부를 확인하는 메서드
	 */
	boolean existsByReservationId(final Long reservationId);

	/**
	 * 리뷰 조회 메서드
	 *
	 * @param postId 선상 낚시 게시글 ID
	 * @return {@link Page<ReviewWithMemberResponse>}
	 * @implSpec 게시글 ID를 기반으로 작성된 리뷰 조회
	 */
	Page<ReviewWithMemberResponse> findReviewsWithMemberByPostId(@Param("postId") final Long postId, final Pageable pageable);

	/**
	 * 내가 작성한 리뷰 조회
	 *
	 * @param memberId 회원 ID
	 * @return {@link Page<ReviewWithMemberResponse>}
	 * @implSpec 회원 ID를 기반으로 작성된 리뷰 조회
	 */
	Page<ReviewWithMemberResponse> findReviewsWithMemberByMemberId(@Param("memberId") final Long memberId, final Pageable pageable);
}

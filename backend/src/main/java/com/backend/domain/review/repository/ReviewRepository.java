package com.backend.domain.review.repository;

import com.backend.domain.review.entity.Review;

public interface ReviewRepository {

	/**
	 * 리뷰 저장 메서드
	 *
	 * @param review {@link Review}
	 * @return {@link Review}
	 * @implSpec 선상 낚시에 대한 리뷰 저장 메서드
	 */
	Review save(Review review);

	/**
	 * 예약에 리뷰가 있는지 검증 하는 메서드
	 *
	 * @param reservationId 예약 ID
	 * @return 예약에 리뷰가 존재하면 true, 없으면 false
	 * @implSpec 예약 ID를 기반으로 해당 예약에 대한 리뷰 존재 여부를 확인하는 메서드
	 */
	boolean existsByReservationId(Long reservationId);
}

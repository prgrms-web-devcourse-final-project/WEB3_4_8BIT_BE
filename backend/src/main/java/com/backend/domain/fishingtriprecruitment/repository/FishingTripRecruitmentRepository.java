package com.backend.domain.fishingtriprecruitment.repository;

import java.util.Optional;

import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;

public interface FishingTripRecruitmentRepository {

	/**
	 * 동출 모집 신청 저장 메서드
	 *
	 * @param fishingTripRecruitment {@link FishingTripRecruitment}
	 * @return {@link FishingTripRecruitment}
	 * @implSpec FishingTripRecruitment 받아서 전환후 저장된 엔티티 반환
	 */

	FishingTripRecruitment save(final FishingTripRecruitment fishingTripRecruitment);

	/**
	 *
	 * @param fishingTripRecruitmentId {@link Long}
	 * @return {@link Optional<FishingTripRecruitment>}
	 * @implSpec fishingTripRecruitmentId로 동출 모집 신청 내역 조회
	 */
	Optional<FishingTripRecruitment> findById(final Long fishingTripRecruitmentId);
}

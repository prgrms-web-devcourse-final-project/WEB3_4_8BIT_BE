package com.backend.domain.fishingtriprecruitment.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
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

	/**
	 * 주어진 동출 게시글 ID와 모집 상태에 해당하는 신청자 목록을 조회합니다.
	 *
	 * <p>이 메서드는 특정 모집글에 대해 특정 상태(PENDING, APPROVED, REJECTED 등)를 가진
	 * 신청자들의 정보를 {@link FishingTripRecruitmentResponse.DetailPageQueryDto} 형태로 반환합니다.</p>
	 *
	 * <p>신청자는 신청한 순서대로 정렬되어 반환되며, 정렬 기준은 {@code fishingTripRecruitmentId}의 오름차순입니다.
	 * 즉, 먼저 신청한 사용자가 먼저 조회됩니다.</p>
	 *
	 * @implSpec
	 * 이 구현은 {@code fishing_trip_recruitments} 테이블에서 {@code fishingTripPostId}와
	 * {@code recruitmentStatus}로 필터링하고, {@code member} 테이블을 조인하여 사용자 닉네임과 프로필 이미지를 함께 조회합니다.
	 * 정렬은 {@code fishingTripRecruitment.createdAt} 또는 {@code fishingTripRecruitmentId} 기준으로 오름차순 처리되며,
	 * QueryDSL 기반의 정적 쿼리를 사용합니다.
	 *
	 * @param fishingTripPostId 동출 게시글의 고유 ID
	 * @param status 조회할 신청 상태 (예: PENDING, APPROVED, REJECTED)
	 * @return 조건에 해당하는 신청자 정보 목록
	 */
	List<FishingTripRecruitmentResponse.DetailPageQueryDto> findDetailPageQueryDtoByIdAndStatus(
		final Long fishingTripPostId,
		final RecruitmentStatus status
	);
}

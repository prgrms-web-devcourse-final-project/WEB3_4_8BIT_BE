package com.backend.domain.fishingtriprecruitment.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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
	 * 특정 모집글의 신청자 목록을 커서 기반 페이징 방식으로 조회합니다.
	 *
	 * <p>이 메서드는 동출 모집글 ID와 모집 상태(PENDING, APPROVED, REJECTED)를 기준으로,
	 * 해당 조건에 맞는 신청자들을 커서 기반으로 조회하여 {@link ScrollResponse} 형태로 반환합니다.
	 * 페이징 정보는 {@link GlobalRequest.CursorRequest}를 통해 전달받으며,
	 * 기본 정렬 기준은 {@code fishingTripRecruitmentId} 오름차순입니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이징 요청 정보 (id, size 등)
	 * @param fishingTripPostId 조회 대상이 되는 동출 모집 게시글 ID
	 * @param status 조회할 모집 상태 (APPROVED, PENDING, REJECTED)
	 * @return 페이징된 신청자 목록
	 */
	ScrollResponse<FishingTripRecruitmentResponse.DetailPage> findDetailPageByFishingTripPostIdAndStatus(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId,
		final RecruitmentStatus status
	);
}

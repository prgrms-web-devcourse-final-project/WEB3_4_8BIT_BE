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

	/**
	 * 주어진 낚시 게시글 ID에 해당하는 승인된 신청자들의 회원 ID 목록을 조회합니다.
	 *
	 * <p>모집 신청 테이블에서 {@code fishingTripPostId}에 해당하는 게시글에 대해
	 * 신청 상태가 {@link RecruitmentStatus#APPROVED}인 회원들의 ID만 추출하여 반환합니다.</p>
	 *
	 * @param fishingTripPostId 신청자를 조회할 대상 낚시 게시글 ID
	 * @return 승인된 신청자의 회원 ID 목록 (없을 경우 빈 리스트 반환)
	 * @implSpec 내부적으로 {@code fishingTripRecruitment.fishingTripPostId = :fishingTripPostId}
	 *           AND {@code recruitmentStatus = 'APPROVED'} 조건으로 쿼리하여,
	 *           {@code memberId} 컬럼만 조회합니다.
	 */
	List<Long> findMemberIdListByPostId(final Long fishingTripPostId);

	/**
	 * 특정 동출 게시글 ID에 해당하는 모든 동출 신청 데이터를 삭제하는 메서드입니다.
	 * <p>벌크 쿼리를 사용하여 성능 저하 없이 삭제를 처리합니다.</p>
	 *
	 * @param fishingTripPostId 삭제할 동출 게시글의 ID
	 */
	void deleteAllByPostId(final Long fishingTripPostId);
}

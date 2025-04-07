package com.backend.domain.fishingtriprecruitment.service;

import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;

public interface FishingTripRecruitmentService {

	/**
	 * 동출 게시글 저장 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripRecruitmentRequest.Create}
	 * @return {@link Long fishingTripRecruitmentId} Long: 동출 모집 신청 Id fishingTripRecruitmentId
	 * @implSpec 로그인한 멤버 Id와 동출 모집 신청에 필요한 정보를 받아 모집 신청 작성
	 */

	Long createFishingTripRecruitment(
		final Long memberId,
		final FishingTripRecruitmentRequest.Create requestDto
	);

	/**
	 * 낚시 동행 신청을 거절합니다.
	 *
	 * <p>이 메서드는 특정 낚시 모집글에 대해 들어온 개별 신청을 거절할 때 사용되며,
	 * 신청을 거절할 권한이 있는 작성자인지를 먼저 검증한 후, 해당 신청의 상태를 {@code REJECTED}로 변경합니다.</p>
	 *
	 * @implSpec
	 * <ol>
	 *     <li>해당 신청 {@code fishingTripRecruitmentId}를 통해 모집글 ID를 조회합니다.</li>
	 *     <li>모집글 작성자 ID와 현재 로그인한 사용자 ID {@code memberId}가 일치하는지 검증합니다.</li>
	 *     <li>검증 통과 시, 신청 상태를 {@code REJECTED}로 변경합니다.</li>
	 *     <li>신청이 존재하지 않거나 권한이 없을 경우, 관련 예외를 발생시킵니다.</li>
	 * </ol>
	 *
	 * @param memberId 현재 로그인한 사용자(모집글 작성자)의 ID
	 * @param fishingTripRecruitmentId 거절할 낚시 동행 신청의 고유 ID
	 * @throws FishingTripPostException 작성자가 아닐 경우 또는 모집글/신청이 존재하지 않을 경우 발생
	 */
	void refuseFishingTripRecruitment(final Long memberId, final Long fishingTripRecruitmentId);
}

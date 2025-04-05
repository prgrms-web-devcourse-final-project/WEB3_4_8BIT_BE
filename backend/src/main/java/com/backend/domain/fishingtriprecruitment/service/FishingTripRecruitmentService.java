package com.backend.domain.fishingtriprecruitment.service;

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

}

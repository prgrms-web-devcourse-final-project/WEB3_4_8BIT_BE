package com.backend.domain.fishingtriprecruitment.converter;

import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;

public class FishingTripRecruitmentConverter {

	/**
	 * 로그인한 멤버가 동출 모집 신청 Dto를 Entity로 변환 메서드
	 *
	 * @param memberId   {@link Long}
	 * @param requestDto {@link FishingTripRecruitmentRequest.Create}
	 * @return {@link FishingTripRecruitment}
	 */

	public static FishingTripRecruitment fromFishingTripRecruitmentCreate(
		final Long memberId,
		final FishingTripRecruitmentRequest.Create requestDto
	) {
		return FishingTripRecruitment.builder()
			.fishingLevel(FishingLevel.from(requestDto.fishingLevel()))
			.introduction(requestDto.introduction())
			.recruitmentStatus(RecruitmentStatus.PENDING) // 신청시 "대기"로 기본값
			.fishingTripPostId(requestDto.fishingTripPostId())
			.memberId(memberId)
			.build();
	}
}

package com.backend.domain.fishingtrippost.converter;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;

public class FishingTripPostConverter {

	/**
	 * 로그인한 멤버가 모집 게시글 작성 Dto를 Entity로 변환 메서드
	 *
	 * @param memberId   {@link Long}
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link FishingTripPost}
	 */

	public static FishingTripPost fromFishingTripPostCreate(
		final Long memberId,
		final FishingTripPostRequest.Form requestDto
	) {
		return FishingTripPost.builder()
			.subject(requestDto.subject())
			.content(requestDto.content())
			.recruitmentCount(requestDto.recruitmentCount())
			.currentCount(0)
			.isShipFish(requestDto.isShipFish())
			.fishingDate(requestDto.fishingDate())
			.fishingPointId(requestDto.fishingPointId())
			.memberId(memberId)
			.fileIdList(requestDto.fileIdList())
			.build();
	}
}

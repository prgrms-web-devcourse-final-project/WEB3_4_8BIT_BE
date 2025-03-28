package com.backend.domain.shipfishingpost.converter;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public class ShipFishingPostConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 *
	 * @param requestDto
	 * @param durationTime
	 * @return {@link ShipFishingPost}
	 */
	public static ShipFishingPost fromShipFishPostsRequestCreate(
		ShipFishingPostRequest.Create requestDto,
		String durationTime) {

		return ShipFishingPost.builder()
			.subject(requestDto.subject())
			.content(requestDto.content())
			.images(requestDto.images())
			.price(requestDto.price())
			.location(requestDto.location())
			.startTime(requestDto.startTime())
			.endTime(requestDto.endTime())
			.durationTime(durationTime)
			.guestMaxCount(requestDto.maxGuestCount())
			.fishId(requestDto.fishIds())
			.shipId(requestDto.shipId())
			.reviewEverRate(0D)
			.build();
	}
}

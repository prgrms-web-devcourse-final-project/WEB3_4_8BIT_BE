package com.backend.domain.shipfishposts.converter;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;

public class ShipFishPostsConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 *
	 * @param requestDto
	 * @param durationTime
	 * @return {@link ShipFishPosts}
	 */
	public static ShipFishPosts fromShipFishPostsRequestCreate(
		ShipFishPostsRequest.Create requestDto,
		String durationTime) {

		return ShipFishPosts.builder()
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

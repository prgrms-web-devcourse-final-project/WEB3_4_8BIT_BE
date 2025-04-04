package com.backend.domain.shipfishingpost.converter;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public class ShipFishingPostConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto 를 Entity 로 변환한다.
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link ShipFishingPost}
	 */
	public static ShipFishingPost fromShipFishingPostRequestCreate(
		final ShipFishingPostRequest.Create requestDto,
		final Long memberId) {

		ShipFishingPost shipFishingPost = ShipFishingPost.builder()
			.memberId(memberId)
			.subject(requestDto.subject())
			.content(requestDto.content())
			.imageList(requestDto.images())
			.price(requestDto.price())
			.location(requestDto.location())
			.startTime(requestDto.startTime())
			.endTime(requestDto.endTime())
			.maxGuestCount(requestDto.maxGuestCount())
			.fishList(requestDto.fishList())
			.shipId(requestDto.shipId())
			.build();

		shipFishingPost.setDurationTime();

		return shipFishingPost;
	}
}

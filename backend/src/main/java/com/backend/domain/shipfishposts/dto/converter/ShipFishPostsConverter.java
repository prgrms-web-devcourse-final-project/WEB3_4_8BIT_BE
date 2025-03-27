package com.backend.domain.shipfishposts.dto.converter;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;

public class ShipFishPostsConverter {

	/**
	 *
	 * @param requestDto
	 * @param durationMinute
	 * @return {@link ShipFishPosts}
	 * @implSpec 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 */
	public static ShipFishPosts fromShipFishPostsRequestCreate(
		ShipFishPostsRequest.Create requestDto,
		Long durationMinute) {

		return ShipFishPosts.builder()
			.subject(requestDto.subject())
			.content(requestDto.content())
			.images(requestDto.images())
			.price(requestDto.price())
			.startDate(requestDto.startDate())
			.endDate(requestDto.endDate())
			.durationMinute(durationMinute)
			.fishId(requestDto.fishIds())
			.shipId(requestDto.shipId())
			.reviewEverRate(0D)
			.build();
	}
}

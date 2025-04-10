package com.backend.domain.shipfishingpost.converter;

import java.util.List;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
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
			.fileIdList(requestDto.fileIdList())
			.price(requestDto.price())
			.location(requestDto.location())
			.startTime(requestDto.startTime())
			.endTime(requestDto.endTime())
			.maxGuestCount(requestDto.maxGuestCount())
			.fishIdList(requestDto.fishIdList())
			.shipId(requestDto.shipId())
			.build();

		shipFishingPost.setDurationTime();

		return shipFishingPost;
	}

	public static ShipFishingPostResponse.DetailWithFileUrlAndFishName fromDetailWithFileUrlAndFishName(
		final ShipFishingPostResponse.DetailAll detail,
		final List<String> fileUrlList,
		final List<FishResponse.Summary> fishInfoList) {

		return ShipFishingPostResponse.DetailWithFileUrlAndFishName.builder()
			.shipFishingPostId(detail.detailShipFishingPost().shipFishingPostId())
			.subject(detail.detailShipFishingPost().subject())
			.content(detail.detailShipFishingPost().content())
			.price(detail.detailShipFishingPost().price())
			.fileUrlList(fileUrlList)
			.startTime(detail.detailShipFishingPost().startTime())
			.durationTime(detail.detailShipFishingPost().durationTime())
			.maxGuestCount(detail.detailShipFishingPost().maxGuestCount())
			.reviewEverRate(detail.detailShipFishingPost().reviewEverRate())
			.detailFish(fishInfoList)
			.detailShip(detail.detailShip())
			.detailMember(detail.detailMember())
			.build();
	}
}

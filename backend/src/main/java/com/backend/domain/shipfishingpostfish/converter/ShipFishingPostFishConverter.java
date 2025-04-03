package com.backend.domain.shipfishingpostfish.converter;

import java.util.ArrayList;
import java.util.List;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;

public class ShipFishingPostFishConverter {

	/**
	 * 선상 낚시 게시글과 연관된 물고기 Id를 Entity 로 변환한다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param fishId {@link Long}
	 * @return {@link ShipFishingPostFish}
	 */
	public static ShipFishingPostFish fromShipFishingPostFishRequestFishId(
		final Long shipFishingPostId,
		final Long fishId) {

		return ShipFishingPostFish.builder()
			.shipFishingPostId(shipFishingPostId)
			.fishId(fishId)
			.build();
	}

	/**
	 * 선상 낚시 게시글과 연관된 물고기 Id를 Entity 로 변환 후 리스트로 반환한다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param fishIdList {@link List<Long>}
	 * @return {@link List<ShipFishingPostFish>}
	 */
	public static List<ShipFishingPostFish> fromShipFishingPostFishRequestFishIdList(
		final Long shipFishingPostId,
		final List<Long> fishIdList
	) {
		List<ShipFishingPostFish> shipFishingPostFishList = new ArrayList<>();

		for (Long fishId : fishIdList) {
			shipFishingPostFishList.add(fromShipFishingPostFishRequestFishId(shipFishingPostId, fishId));
		}

		return shipFishingPostFishList;
	}

}

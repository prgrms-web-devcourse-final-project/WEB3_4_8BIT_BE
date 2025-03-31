package com.backend.domain.fishencyclopedia.converter;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;

public class FishEncyclopediaConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 *
	 * @param create   {@link FishEncyclopediaRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link FishEncyclopedia}
	 */
	public static FishEncyclopedia fromFishEncyclopediasRequestCreate(
		final FishEncyclopediaRequest.Create create,
		final Long memberId
	) {

		return FishEncyclopedia.builder()
			.fishEncyclopediaId(create.fishId())
			.length(create.length())
			.count(create.count())
			.fishPointId(create.fishPointId())
			.memberId(memberId)
			.build();
	}

}

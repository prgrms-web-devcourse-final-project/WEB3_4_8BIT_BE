package com.backend.domain.fishencyclopedia.converter;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediasRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;

public class FishEncyclopediasConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 *
	 * @param create   {@link FishEncyclopediasRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link FishEncyclopedias}
	 */
	public static FishEncyclopedias fromFishEncyclopediasRequestCreate(
		FishEncyclopediasRequest.Create create,
		Long memberId
	) {

		return FishEncyclopedias.builder()
			.fishEncyclopediaId(create.fishId())
			.length(create.length())
			.fishPointId(create.fishPointId())
			.memberId(memberId)
			.build();
	}

}

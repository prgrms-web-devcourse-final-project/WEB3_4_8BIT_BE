package com.backend.domain.fishencyclopedia.converter;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;

public class FishEncyclopediaConverter {

	/**
	 * 선상 낚시 게시글 생성 Dto를 Entity로 변환한다.
	 *
	 * @param requestDto   {@link FishEncyclopediaRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link FishEncyclopedia}
	 */
	public static FishEncyclopedia fromFishEncyclopediasRequestCreate(
		final FishEncyclopediaRequest.Create requestDto,
		final Long memberId
	) {

		return FishEncyclopedia.builder()
			.fishEncyclopediaId(requestDto.fishId())
			.length(requestDto.length())
			.count(requestDto.count())
			.fishPointId(requestDto.fishPointId())
			.memberId(memberId)
			.build();
	}

}

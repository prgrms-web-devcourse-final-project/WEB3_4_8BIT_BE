package com.backend.domain.fishencyclopedia.dto.converter;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediasRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;

public class FishEncyclopediasConverter {

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

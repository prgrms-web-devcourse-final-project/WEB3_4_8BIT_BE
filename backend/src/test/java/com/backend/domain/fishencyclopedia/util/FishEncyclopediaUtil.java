package com.backend.domain.fishencyclopedia.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;

public class FishEncyclopediaUtil {

	public static FishEncyclopedias createFishEncyclopediaWithNullableId() {

		return FishEncyclopedias.builder()
			.fishId(1L)
			.memberId(1L)
			.fishPointId(1L)
			.length(30)
			.build();
	}

	public static FishEncyclopedias createFishEncyclopediaWithId() {

		return FishEncyclopedias.builder()
			.fishEncyclopediaId(1L)
			.fishId(1L)
			.memberId(1L)
			.fishPointId(1L)
			.length(30)
			.build();
	}

	public static List<FishEncyclopedias> createFishEncyclopediaList(Long range) {
		List<FishEncyclopedias> fishEncyclopediasList = new ArrayList<>();

		LongStream.range(0, range).forEach(i -> {
			FishEncyclopedias fishEncyclopedias = FishEncyclopedias.builder()
				.fishEncyclopediaId(range + 1)
				.fishId(range + 1)
				.memberId(range + 1)
				.fishPointId(range + 1)
				.length((range.intValue() + 1))
				.build();

			fishEncyclopediasList.add(fishEncyclopedias);
		});

		return fishEncyclopediasList;
	}
}

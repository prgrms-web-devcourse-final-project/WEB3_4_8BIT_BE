package com.backend.domain.fishpointsummary.converter;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;

public class FishPointSummaryConverter {

	public static FishPointSummary fromCreate(
		final Long fishPointId,
		final Long fishId,
		final Long fileId,
		final Integer totalCount
	) {
		return FishPointSummary.builder()
			.fishPointId(fishPointId)
			.fishId(fishId)
			.fileId(fileId)
			.totalCount(totalCount)
			.build();
	}
}

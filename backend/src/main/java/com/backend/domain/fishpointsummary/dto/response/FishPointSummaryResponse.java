package com.backend.domain.fishpointsummary.dto.response;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

public class FishPointSummaryResponse {

	public record Basic (
		Long fishId,
		String fishName,
		String fileUrl,
		List<Long> spawnSeasonList,
		Integer totalCount
	) {
		@QueryProjection
		public Basic {}
	}
}

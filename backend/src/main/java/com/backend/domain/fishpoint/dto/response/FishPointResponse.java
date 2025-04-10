package com.backend.domain.fishpoint.dto.response;

import java.util.List;

import com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class FishPointResponse {

	public record Basic(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Double latitude,
		Double longitude,
		Boolean isBan
	) {
		@QueryProjection
		public Basic {}
	}

	public record WithDistance(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Double latitude,
		Double longitude,
		Boolean isBan,
		Double distance
	) {
		@QueryProjection
		public WithDistance {}
	}

	public record Popularity(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Long recruitmentCount
	) {
		@QueryProjection
		public Popularity {}
	}

	@Builder
	public record Detail(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Double latitude,
		Double longitude,
		Boolean isBan,
		List<FishPointSummaryResponse.Basic> fishList
	) {}
}

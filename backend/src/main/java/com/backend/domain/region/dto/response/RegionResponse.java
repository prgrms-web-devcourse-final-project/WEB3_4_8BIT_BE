package com.backend.domain.region.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class RegionResponse {

	@Builder
	public record Basic(
		Long regionId,
		String regionName,
		Double latitude,
		Double longitude
	) {
		@QueryProjection
		public Basic {}
	}
}

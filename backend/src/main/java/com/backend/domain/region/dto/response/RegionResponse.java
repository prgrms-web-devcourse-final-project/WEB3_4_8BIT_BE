package com.backend.domain.region.dto.response;

import com.backend.domain.region.entity.RegionType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class RegionResponse {

	@Builder
	public record Basic(
		Long regionId,
		String regionName
	) {
		@QueryProjection
		public Basic {}
	}
}

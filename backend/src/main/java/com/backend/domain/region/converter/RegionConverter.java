package com.backend.domain.region.converter;

import static com.backend.domain.region.dto.response.RegionResponse.*;

import com.backend.domain.region.entity.Region;

public class RegionConverter {

	public static Basic from(final Region region) {

		return Basic.builder()
			.regionId(region.getRegionId())
			.regionName(region.getType().getName())
			.type(region.getType())
			.build();
	}
}

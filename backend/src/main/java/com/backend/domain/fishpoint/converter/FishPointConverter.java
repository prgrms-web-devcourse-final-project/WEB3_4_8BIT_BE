package com.backend.domain.fishpoint.converter;

import java.util.List;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse;

import lombok.Builder;

@Builder
public class FishPointConverter {

	public static FishPointResponse.Detail fromEntityAndSummary(
		final FishPoint fishPoint,
		final List<FishPointSummaryResponse.Basic> fishPointSummaryResponseBasicList
	) {
		return FishPointResponse.Detail.builder()
			.fishPointId(fishPoint.getFishPointId())
			.fishPointName(fishPoint.getFishPointName())
			.fishPointDetailName(fishPoint.getFishPointDetailName())
			.latitude(fishPoint.getLatitude())
			.longitude(fishPoint.getLongitude())
			.isBan(fishPoint.getIsBan())
			.fishList(fishPointSummaryResponseBasicList)
			.build();
	}
}

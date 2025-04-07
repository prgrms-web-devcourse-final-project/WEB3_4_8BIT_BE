package com.backend.domain.fishpoint.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record FishPointResponse(
	Long fishPointId,
	String fishPointName,
	String fishPointDetailName,
	Double latitude,
	Double longitude,
	Boolean isBan
) {
	@QueryProjection
	public FishPointResponse {}
}

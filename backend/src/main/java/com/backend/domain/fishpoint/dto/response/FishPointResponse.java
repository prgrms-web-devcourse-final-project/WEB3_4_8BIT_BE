package com.backend.domain.fishpoint.dto.response;

import com.querydsl.core.annotations.QueryProjection;


public class FishPointResponse {

	public record Response(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Double latitude,
		Double longitude,
		Boolean isBan
	) {
		@QueryProjection
		public Response {}
	}

	public record ResponseWithDistance(
		Long fishPointId,
		String fishPointName,
		String fishPointDetailName,
		Double latitude,
		Double longitude,
		Boolean isBan,
		Double distance
	) {
		@QueryProjection
		public ResponseWithDistance {}
	}
}

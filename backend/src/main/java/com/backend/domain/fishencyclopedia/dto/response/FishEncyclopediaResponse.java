package com.backend.domain.fishencyclopedia.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

public class FishEncyclopediaResponse {
	public record Detail(
		Integer length,
		Integer count,
		String fishPointName,
		String fishPointDetailName,
		ZonedDateTime createdAt
	) {
		@QueryProjection
		public Detail {
		}
	}
}

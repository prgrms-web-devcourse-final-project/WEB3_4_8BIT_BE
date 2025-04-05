package com.backend.domain.fish.dto;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

public class FishResponse {

	public record Detail(
		Long fishId,
		String name,
		String description,
		String fileUrl,
		List<Long> spawnSeasonList,
		String spawnLocation
		) {

		@QueryProjection
		public Detail {
		}

	}
}

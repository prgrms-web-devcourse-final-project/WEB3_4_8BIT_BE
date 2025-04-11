package com.backend.domain.activityhistory.dto.response;

import java.time.ZonedDateTime;

import com.backend.domain.activityhistory.domain.ActivityType;
import com.querydsl.core.annotations.QueryProjection;

public class ActivityHistoryResponse {

	public record Detail(
		Long activityHistoryId,
		ActivityType activityType,
		String activityTypeKr,
		String description,
		ZonedDateTime createdAt
	) {

		@QueryProjection
		public Detail {
			activityTypeKr = activityType.getDisplayName();
		}
	}
}

package com.backend.domain.activityhistory.dto.request;

import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.global.validator.ValidEnum;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActivityHistoryRequest {

	/**
	 * {@code
	 * {
	 * "activityType": FISHING_TRIP_POST,
	 * }
	 * }
	 *
	 * @param activityType
	 */
	public record Search(
		@ValidEnum(enumClass = ActivityType.class, nullable = true)
		@Schema(description = "활동 타입 (FISHING_TRIP_POST, RESERVATION, FISH_ENCYCLOPEDIA")
		ActivityType activityType
	) {

	}
}

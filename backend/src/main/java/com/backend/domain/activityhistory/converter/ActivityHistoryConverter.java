package com.backend.domain.activityhistory.converter;

import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.domain.activityhistory.entity.ActivityHistory;

public class ActivityHistoryConverter {

	public static ActivityHistory from(
		final ActivityType activityType,
		final Long targetId,
		final String description
	) {
		return ActivityHistory.builder()
			.activityType(activityType)
			.targetId(targetId)
			.description(description)
			.build();
	}
}

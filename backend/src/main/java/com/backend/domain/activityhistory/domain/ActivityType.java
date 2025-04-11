package com.backend.domain.activityhistory.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {
	FISHING_TRIP_POST("동출 모집"),
	RESERVATION("예약"),
	FISH_ENCYCLOPEDIA("어류 도감");

	private final String displayName;

	@JsonCreator
	public static ActivityType from(String param) {
		return Stream.of(ActivityType.values())
			.filter(a ->
				a.name().equalsIgnoreCase(param) || a.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

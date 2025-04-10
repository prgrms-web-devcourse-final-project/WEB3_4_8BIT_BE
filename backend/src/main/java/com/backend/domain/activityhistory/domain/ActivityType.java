package com.backend.domain.activityhistory.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ActivityType {
	FISHING_TRIP_POST("동출 모집"),
	RESERVATION("예약"),
	FISH_ENCYCLOPEDIA("어류 도감");

	private final String displayName;

	@JsonValue
	public String getDisplayName() {
		return displayName;
	}
}

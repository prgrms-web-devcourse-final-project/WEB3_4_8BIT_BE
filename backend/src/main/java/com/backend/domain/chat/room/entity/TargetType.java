package com.backend.domain.chat.room.entity;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 대상 타입")
public enum TargetType {

	FISHING_TRIP_POST("동출 게시글"),
	FISH_POINT("낚시 포인트");

	private final String displayName;

	TargetType(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return displayName;
	}

	@JsonCreator
	public static TargetType from(String param) {
		return Stream.of(TargetType.values())
			.filter(t ->
				t.name().equalsIgnoreCase(param) || t.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

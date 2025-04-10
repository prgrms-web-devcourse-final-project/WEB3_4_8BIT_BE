package com.backend.domain.chat.room.entity;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 상태")
public enum Status {

	ACTIVE("활성"),
	CLOSED("종료");

	private final String displayName;

	Status(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return displayName;
	}

	@JsonCreator
	public static Status from(String param) {
		return Stream.of(Status.values())
			.filter(s ->
				s.name().equalsIgnoreCase(param) || s.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

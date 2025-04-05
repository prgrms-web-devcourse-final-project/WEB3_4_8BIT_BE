package com.backend.domain.fishingtriprecruitment.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "낚시 실력")
public enum FishingLevel {

	BEGINNER("초급"),
	INTERMEDIATE("중급"),
	ADVANCED("고급");

	private final String displayName;

	FishingLevel(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return displayName;
	}

	@JsonCreator
	public static FishingLevel from(String param) {
		return Stream.of(FishingLevel.values())
			.filter(l ->
				l.name().equalsIgnoreCase(param) || l.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

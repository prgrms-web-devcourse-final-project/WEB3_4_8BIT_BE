package com.backend.domain.fishingtrippost.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "모집 진행 상태")
public enum PostStatus {

	RECRUITING("모집중"),
	COMPLETED("모집완료");

	private final String displayName;

	PostStatus(String displayName) {
		this.displayName = displayName;
	}

	@JsonCreator
	public static PostStatus from(String param) {
		return Stream.of(PostStatus.values())
			.filter(s ->
				s.name().equalsIgnoreCase(param) || s.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

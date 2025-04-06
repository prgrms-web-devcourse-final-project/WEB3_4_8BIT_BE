package com.backend.domain.fishingtriprecruitment.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "모집 상태")
public enum RecruitmentStatus {

	APPROVED("승인"),
	PENDING("대기"),
	REJECTED("거절");

	private final String displayName;

	RecruitmentStatus(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return displayName;
	}

	@JsonCreator
	public static RecruitmentStatus from(String param) {
		return Stream.of(RecruitmentStatus.values())
			.filter(s ->
				s.name().equalsIgnoreCase(param) || s.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

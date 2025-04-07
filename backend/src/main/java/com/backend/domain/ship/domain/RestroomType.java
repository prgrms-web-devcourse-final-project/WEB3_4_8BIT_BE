package com.backend.domain.ship.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 화장실 타입을 정의한 enum 클래스 입니다.
 *
 * PUBLIC - 공용
 * SEPARATION - 구분
 * NONE - 없음
 */
public enum RestroomType {
	PUBLIC("공용 화장실"),
	SEPARATION("남/여 구분 화장실"),
	NONE("없음");

	private final String displayName;

	RestroomType(String displayName) {
		this.displayName = displayName;
	}

	@JsonValue
	public String getDisplayName() {
		return this.displayName;
	}

	@JsonCreator
	public static RestroomType from(String param) {
		return Stream.of(RestroomType.values())
			.filter(l ->
				l.name().equalsIgnoreCase(param) || l.getDisplayName().equalsIgnoreCase(param)
			)
			.findFirst()
			.orElse(null);
	}
}

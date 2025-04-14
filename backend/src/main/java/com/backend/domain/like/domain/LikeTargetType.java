package com.backend.domain.like.domain;

import lombok.Getter;

@Getter
public enum LikeTargetType {
	SHIP_FISHING_POST,
	FISHING_TRIP_POST;

	public String getTableName() {
		return switch (this) {
			case SHIP_FISHING_POST -> "ship_fishing_posts";
			case FISHING_TRIP_POST -> "fishing_trip_posts";
		};
	}

	public String getIdColumn() {
		return switch (this) {
			case SHIP_FISHING_POST -> "ship_fishing_post_id";
			case FISHING_TRIP_POST -> "fishing_trip_post_id";
		};
	}
}

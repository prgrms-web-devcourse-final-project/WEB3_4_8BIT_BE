package com.backend.domain.like.dto.response;

import com.backend.domain.like.domain.LikeTargetType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class LikeResponse {

	/**
	 * <pre>
	 * {@code
	 * {
	 * "likeId": 1,
	 * "targetType": "SHIP_FISHING_POST",
	 * "targetId": 100
	 * }
	 * }
	 * </pre>
	 *
	 * @param likeId     좋아요 ID
	 * @param memberId 	 좋아요 누른 멤버 ID
	 * @param targetType 좋아요 대상 타입 (SHIP_FISHING_POST, FISHING_TRIP_POST)
	 * @param targetId   좋아요 대상 ID
	 */
	@Builder
	public record Detail(
		Long likeId,
		Long memberId,
		LikeTargetType targetType,
		Long targetId
	) {
		@QueryProjection
		public Detail {
		}
	}
}

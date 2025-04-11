package com.backend.domain.like.dto.request;

import com.backend.domain.like.domain.LikeTargetType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 좋아요 요청 DTO
 * <p>
 * {@code
 * {
 * "targetType": "SHIP_FISHING_POST",
 * "targetId": 123
 * }
 * }
 *
 * @param targetType 좋아요 대상 타입 (SHIP_FISHING_POST, FISHING_TRIP_POST) (필수)
 * @param targetId   좋아요 대상 ID (필수)
 */
@Builder
public record LikeRequest(

	@NotNull(message = "대상 타입은 필수입니다.")
	@Schema(description = "좋아요 대상 타입", example = "SHIP_FISHING_POST")
	LikeTargetType targetType,

	@NotNull(message = "대상 ID는 필수입니다.")
	@Schema(description = "좋아요 대상 ID", example = "123")
	Long targetId

) {
}

package com.backend.domain.fish.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FishRequest {
	/**
	 * {
	 * "size": 1,
	 * }
	 *
	 * @param size 가져올 데이터 개수
	 * @author Kim Dong O
	 */
	public record Popular(
		@Min(value = 1, message = "사이즈는 1개 이상이어야 합니다.")
		@Max(value = 10, message = "사이즈는 10개 이하여야 합니다.")
		@NotNull(message = "사이즈는 필수 항목입니다.")
		@Schema(description = "가져올 데이터 개수", example = "4")
		Integer size
	) {

	}
}

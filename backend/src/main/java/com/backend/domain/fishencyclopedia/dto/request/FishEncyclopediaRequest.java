package com.backend.domain.fishencyclopedia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FishEncyclopediaRequest {
	/**
	 * {
	 * "fishId": 1,
	 * "fishPointId: 1,
	 * "length": 10
	 * }
	 *
	 * @param fishId      물고기 ID
	 * @param length      물고기 크기
	 * @param fishPointId 낚시 포인트 ID
	 * @author Kim Dong O
	 */
	public record Create(
		@NotNull(message = "물고기 ID는 필수 항목입니다.")
		@Schema(description = "물고기 ID", example = "1")
		Long fishId,

		@Min(value = 1, message = "물고기 길이는 1cm 이상이어야 합니다.")
		@NotNull(message = "물고기 길이는 필수 항목입니다.")
		@Schema(description = "물고기 길이", example = "10")
		Integer length,

		@Min(value = 1, message = "잡은 물고기 수는 1마리 이상이어야 합니다.")
		@NotNull(message = "잡은 물고기 수는 필수 항목입니다.")
		@Schema(description = "잡은 물고기 수", example = "2")
		Integer count,

		@NotNull(message = "낚시 포인트 ID는 필수 항목입니다.")
		@Schema(description = "낚시 포인트 ID", example = "2")
		Long fishPointId
	) {
	}

	public record PageRequest(
		@Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
		@Schema(description = "페이지 번호", example = "0")
		Integer page,

		@Min(value = 1, message = "페이지 사이즈는 0 이상이어야 합니다.")
		@Schema(description = "페이지 사이즈", example = "10")
		Integer size,

		@Schema(description = "정렬 필드 [createdAt, count, length] (default createdAt)", example = "createdAt")
		String sort,

		@Schema(description = "정렬 필드 [asc, desc] (default desc)", example = "desc")
		String order) {

		public PageRequest {
			page = (page == null) ? 0 : page;
			size = (size == null) ? 10 : size;
		}
	}
}

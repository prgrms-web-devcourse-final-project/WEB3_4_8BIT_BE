package com.backend.domain.fishingtrippost.dto.request;

import java.time.ZonedDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class FishingTripPostRequest {

	/**
	 * <pre>
	 * {@code
	 * {
	 *   "subject": "낚시 같이 가실 분~",
	 *   "content": "오전 출조 예정입니다. 초보도 환영!",
	 *   "recruitmentCount": 4,
	 *   "isShipFish": true,
	 *   "fishingDate": "2025-05-01T15:00:00+09:00",
	 *   "fishingPointId": 1,
	 *   "fileIdList": [1, 2, 3]
	 * }
	 * }
	 * </pre>
	 * @param subject 게시글 제목 (필수, 최대 50자)
	 * @param content 게시글 내용 (필수, 최대 800자)
	 * @param recruitmentCount 모집 인원 수 (필수, 1 이상)
	 * @param isShipFish 선상 낚시 여부 (필수, true: 선상 / false: 갯바위)
	 * @param fishingDate 출조 시간 (필수, HH:mm 형식)
	 * @param fishingPointId 낚시 포인트 ID (필수)
	 * @param fileIdList 이미지 Id List (선택)
	 */

	@Builder
	public record Form(
		@NotBlank(message = "게시글 제목은 필수 항목입니다.")
		@Size(max = 50, message = "게시글 제목은 최대 50자까지 가능합니다.")
		@Schema(description = "게시글 제목", example = "게시글 제목")
		String subject,

		@NotBlank(message = "게시글 내용은 필수 항목입니다.")
		@Size(max = 800, message = "게시글 내용은 최대 800자까지 가능합니다.")
		@Schema(description = "게시글 내용", example = "게시글 내용")
		String content,

		@Positive(message = "모집 인원은 1 명 이상이어야 합니다.")
		@Schema(description = "모집 인원", example = "6")
		Integer recruitmentCount,

		@NotNull(message = "선상 낚시 여부는 필수 항목입니다.")
		@Schema(description = "선상 낚시 여부 (true: 선상 낚시, false: 낚시)", example = "true")
		Boolean isShipFish,

		@NotNull(message = "시작 시간은 필수 항목입니다.")
		@Schema(description = "시작 시간", example = "2025-04-05T15:00:00+09:00")
		ZonedDateTime fishingDate,

		@NotNull(message = "낚시 포인트 번호는 필수 항목입니다.")
		@Schema(description = "낚시 포인트 Id 값 (entity)", example = "1")
		Long fishingPointId,

		@Schema(description = "이미지 URL Id 리스트", example = "[1, 2, 3]")
		List<Long> fileIdList
	) {
	}
}
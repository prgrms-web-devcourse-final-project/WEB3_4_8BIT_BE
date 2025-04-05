package com.backend.domain.fishingtriprecruitment.dto.request;

import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.global.validator.ValidEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class FishingTripRecruitmentRequest {

	/**
	 * <pre>
	 * {@code
	 * {
	 *   "fishingTripPostId": 1,
	 *   "introduction": "초보지만 잘 부탁드립니다!",
	 *   "fishingLevel": "초급",
	 * }
	 * }
	 * </pre>
	 *
	 * @param fishingTripPostId 모집하려는 동출 게시글 ID (필수)
	 * @param introduction      자기소개 메시지 (필수)
	 * @param fishingLevel      낚시 실력 (필수) - 초급 / 중급 / 고급
	 */
	@Builder
	public record Create(

		@NotNull(message = "동출 게시글 ID는 필수입니다.")
		@Schema(description = "동출 게시글 ID", example = "1")
		Long fishingTripPostId,

		@NotBlank(message = "소개글은 필수입니다.")
		@Size(max = 50, message = "소개글은 최대 50자까지 가능합니다.")
		@Schema(description = "소개글", example = "초보지만 열심히 하겠습니다!")
		String introduction,

		@NotNull(message = "낚시 실력은 필수입니다.")
		@Schema(description = "낚시 실력 (BEGINNER / INTERMEDIATE / ADVANCED)", example = "BEGINNER")
		@ValidEnum(enumClass = FishingLevel.class, message = "올바른 낚시 실력을 입력해주세요.")
		String fishingLevel

	) {
	}
}

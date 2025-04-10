package com.backend.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {

	/**
     * {@code
	 * {
	 *   "content": "어떤 물고기 잡을건가요??",
	 *   "parentId": 1
	 * }
	 *}
	 * @param content 댓글 내용
	 * @param parentId 부모 ID (Null 허용)
	 */
	public record Create(
		@Size(min = 1, max = 100, message = "내용은 1자 이상 100자 이하여야 합니다.")
		@NotBlank(message = "내용은 필수 항목입니다.")
		@Schema(description = "댓글 내용", example = "어떤 물고기 잡을건가요?")
		String content,
		@Schema(description = "부모 댓글 ID", example = "1")
		Long parentId
	) {
	}

	/**
     * {@code
	 * {
	 *   "parentId": 1
	 * }
	 *}
	 * @param parentId 부모 ID (Null 허용)
	 */
	public record Search(
		Long parentId
	) {
	}
}

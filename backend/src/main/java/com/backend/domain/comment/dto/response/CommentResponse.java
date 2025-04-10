package com.backend.domain.comment.dto.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

public class CommentResponse {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Detail(
		Long commentId,
		String content,
		String nickname,
		boolean isAuthor,
		String authorProfileImg,
		ZonedDateTime createdAt,
		Integer childCount,
		Long parentId
	) {
		@QueryProjection
		public Detail {
		}
	}
}

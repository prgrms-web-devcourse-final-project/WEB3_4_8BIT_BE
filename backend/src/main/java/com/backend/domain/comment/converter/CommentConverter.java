package com.backend.domain.comment.converter;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.entity.Comment;

public class CommentConverter {

	public static Comment fromCommentRequestCreate(
		final CommentRequest.Create requestDto,
		final Long memberId,
		final Long fishingTripPostId
	) {
		return Comment.builder()
			.memberId(memberId)
			.parentId(requestDto.parentId())
			.content(requestDto.content())
			.fishingTripPostId(fishingTripPostId)
			.build();
	}
}

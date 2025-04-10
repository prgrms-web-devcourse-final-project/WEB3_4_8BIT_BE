package com.backend.domain.comment.service;

import com.backend.domain.comment.dto.request.CommentRequest;

public interface CommentService {

	/**
	 * 댓글 저장 후 저장된 댓글 ID를 반환합니다.
	 *
	 * @param fishingTripPostId {@link Long}
	 * @param memberId          {@link Long}
	 * @param requestDto        {@link CommentRequest.Create}
	 * @return {@link Long}
	 * @implSpec fishingTripPostId, memberId, requestDto를 받아서 엔티티로 변환 후 저장한 엔티티 ID 반환
	 */
	Long createComment(
		final Long fishingTripPostId,
		final Long memberId,
		final CommentRequest.Create requestDto
	);
}

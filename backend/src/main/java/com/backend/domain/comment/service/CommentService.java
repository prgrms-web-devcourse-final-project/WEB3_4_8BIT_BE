package com.backend.domain.comment.service;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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

	/**
	 * 댓글 전체 조회 메소드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @param memberId          {@link Long}
	 * @param cursorRequestDto  {@link GlobalRequest.CursorRequest}
	 * @param requestDto        {@link CommentRequest.Search}
	 * @return {@link ScrollResponse<CommentResponse.Detail>}
	 * @implSpec 동출 게시글 ID 기준으로 동적 조회 후 결과 값을 반환합니다.
	 * @author Kim Dong O
	 */
	ScrollResponse<CommentResponse.Detail> getDetailList(
		final Long fishingTripPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto
	);

	/**
	 * 댓글 수정 메소드
	 *
	 * @param memberId          {@link Long}
	 * @param commentId         {@link Long}
	 * @param fishingTripPostId {@link Long}
	 * @param requestDto        {@link CommentRequest.Update}
	 * @return {@link ScrollResponse<CommentResponse.Detail>}
	 * @implSpec 작성자인지, 요청한 commentId, fishingTripPostId가 유효한지 검증 후 수정
	 * @author Kim Dong O
	 */
	void updateComment(
		final Long memberId,
		final Long commentId,
		final Long fishingTripPostId,
		final CommentRequest.Update requestDto
	);
}

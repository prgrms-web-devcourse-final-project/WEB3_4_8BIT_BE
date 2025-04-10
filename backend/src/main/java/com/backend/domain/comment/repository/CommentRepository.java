package com.backend.domain.comment.repository;

import java.util.Optional;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.entity.Comment;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface CommentRepository {

	/**
	 * 댓글 저장 메소드
	 *
	 * @param comment {@link Comment}
	 * @return {@link Comment}
	 * @implSpec Comment를 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	Comment save(final Comment comment);

	/**
	 * 댓글 ID로 데이터 존재 여부 확인하는 메소드
	 *
	 * @param commentId {@link Long}
	 * @return {@link Boolean}
	 * @implSpec 댓글 ID로 데이터 존재 여부 확인 후 결과 반환
	 * @author Kim Dong O
	 */
	boolean existsByCommentId(final Long commentId);

	/**
	 * 댓글에 자식 카운트 1개 추가하는 메소드
	 *
	 * @param parentId {@link Long}
	 * @return {@link Boolean}
	 * @implSpec 댓글 ID로 해당 데이터의 자식 카운트 1개 추가하여 업데이트
	 * @author Kim Dong O
	 */
	void addChildCount(final Long parentId);

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
	ScrollResponse<CommentResponse.Detail> findDetailByFishTripPostId(
		final Long fishingTripPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto
	);

	/**
	 * 댓글 ID 조회 메소드
	 *
	 * @param commentId {@link Long}
	 * @return {@link Optional<Comment>}
	 * @implSpec 댓글 ID 조회 후 결과 값 반환
	 * @author Kim Dong O
	 */
	Optional<Comment> findByCommentId(final Long commentId);

	/**
	 * 댓글 ID로 삭제 메소드
	 *
	 * @param commentId {@link Long}
	 * @implSpec 댓글 ID에 해당하는 데이터 삭제
	 * @author Kim Dong O
	 */
	void deleteById(final Long commentId);

	/**
	 * 동출 게시글 ID로 삭제 메소드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @implSpec 동출 게시글 ID에 해당하는 데이터 삭제
	 * @author Kim Dong O
	 */
	void deleteByFishingTripPostId(final Long fishingTripPostId);

	/**
	 * 부모 ID로 삭제 메소드
	 *
	 * @param parentId {@link Long}
	 * @implSpec 부모 ID에 해당하는 데이터 삭제
	 * @author Kim Dong O
	 */
	void deleteByParentId(final Long parentId);
}

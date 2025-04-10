package com.backend.domain.comment.repository;

import com.backend.domain.comment.entity.Comment;

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
}

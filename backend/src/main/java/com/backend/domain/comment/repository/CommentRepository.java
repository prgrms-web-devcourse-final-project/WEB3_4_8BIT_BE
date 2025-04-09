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
}

package com.backend.domain.comment.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.comment.entity.Comment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

	private final CommentJpaRepository commentJpaRepository;
	private final CommentQueryRepository commentQueryRepository;

	@Override
	public Comment save(final Comment comment) {
		return commentJpaRepository.save(comment);
	}

	@Override
	public boolean existsByCommentId(Long commentId) {
		return commentJpaRepository.existsById(commentId);
	}

	@Override
	public void addChildCount(Long parentId) {
		commentQueryRepository.addChildCount(parentId);
	}
}

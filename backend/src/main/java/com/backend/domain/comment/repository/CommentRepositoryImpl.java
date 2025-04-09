package com.backend.domain.comment.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.comment.entity.Comment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

	private final CommentJpaRepository commentJpaRepository;

	@Override
	public Comment save(final Comment comment) {
		return commentJpaRepository.save(comment);
	}
}

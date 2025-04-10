package com.backend.domain.comment.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.entity.Comment;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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
	public boolean existsByCommentId(final Long commentId) {
		return commentJpaRepository.existsById(commentId);
	}

	@Override
	public void addChildCount(final Long parentId) {
		commentQueryRepository.addChildCount(parentId);
	}

	@Override
	public void minusChildCount(final Long parentId) {
		commentQueryRepository.minusChildCount(parentId);
	}

	@Override
	public ScrollResponse<CommentResponse.Detail> findDetailByFishTripPostId(
		final Long fishingTripPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto
	) {
		return commentQueryRepository.findDetailByFishTripPostId(
			fishingTripPostId,
			memberId,
			cursorRequestDto,
			requestDto
		);
	}

	@Override
	public Optional<Comment> findByCommentId(final Long commentId) {
		return commentJpaRepository.findById(commentId);
	}

	@Override
	public void deleteByFishingTripPostId(final Long fishingTripPostId) {
		commentQueryRepository.deleteByFishingTripPostId(fishingTripPostId);
	}

	@Override
	public long deleteByParentId(final Long parentId) {
		return commentQueryRepository.deleteByParentId(parentId);
	}
}

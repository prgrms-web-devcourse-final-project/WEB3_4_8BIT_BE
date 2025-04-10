package com.backend.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.comment.converter.CommentConverter;
import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.comment.exception.CommentErrorCode;
import com.backend.domain.comment.exception.CommentExpection;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final FishingTripPostRepository fishingTripPostRepository;

	@Override
	@Transactional
	public Long createComment(
		final Long fishingTripPostId,
		final Long memberId,
		final CommentRequest.Create requestDto
	) {

		boolean existsByfishingTripPostId = existsByfishingTripPostId(fishingTripPostId);

		validFishingTripPostId(existsByfishingTripPostId);

		if (requestDto.parentId() != null) {
			boolean existsByParentId = existsByParentId(requestDto.parentId());
			validParentId(existsByParentId);
			commentRepository.addChildCount(requestDto.parentId());
		}

		Comment comment = CommentConverter.fromCommentRequestCreate(requestDto, memberId, fishingTripPostId);
		Comment savedComment = commentRepository.save(comment);

		log.debug("댓글 저장: {}", savedComment);

		return savedComment.getCommentId();
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<CommentResponse.Detail> getDetailList(
		final Long fishingTripPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto
	) {

		ScrollResponse<CommentResponse.Detail> getDetailList = commentRepository.findDetailByFishTripPostId(
			fishingTripPostId, memberId, cursorRequestDto, requestDto);

		log.debug("댓글 조회: {}", getDetailList);

		return getDetailList;
	}

	@Override
	@Transactional
	public void updateComment(
		final Long memberId,
		final Long commentId,
		final Long fishingTripPostId,
		final CommentRequest.Update requestDto
	) {
		Comment getComment = getComment(commentId);

		validMemberIdAndFishingTripPostId(memberId, fishingTripPostId, getComment);

		getComment.setContent(requestDto.content());
	}

	private void validMemberIdAndFishingTripPostId(Long memberId, Long fishingTripPostId, Comment getComment) {
		if (!getComment.getMemberId().equals(memberId)) {
			throw new CommentExpection(CommentErrorCode.COMMENT_UNAUTHORIZED_AUTHOR);
		}

		if (!getComment.getFishingTripPostId().equals(fishingTripPostId)) {
			throw new CommentExpection(CommentErrorCode.FISHING_TRIP_ID_NOT_VALID);
		}
	}

	private Comment getComment(Long commentId) {
		return commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new CommentExpection(CommentErrorCode.COMMENT_NOT_FOUND));
	}

	private void validParentId(final boolean existsByParentId) {
		if (!existsByParentId) {
			throw new CommentExpection(CommentErrorCode.PARENT_NOT_FOUND);
		}
	}

	private void validFishingTripPostId(final boolean existsByfishingTripPostId) {
		if (!existsByfishingTripPostId) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND);
		}
	}

	private boolean existsByParentId(final Long parentId) {
		return commentRepository.existsByCommentId(parentId);
	}

	private boolean existsByfishingTripPostId(final Long fishingTripPostId) {
		return fishingTripPostRepository.existsById(fishingTripPostId);
	}
}

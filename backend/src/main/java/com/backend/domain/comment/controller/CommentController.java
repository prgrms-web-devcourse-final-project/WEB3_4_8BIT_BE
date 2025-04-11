package com.backend.domain.comment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.service.CommentService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fishing-trip-post")
@Tag(name = "댓글 API")
public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "댓글 추가하기", description = "댓글 추가시 사용하는 API")
	@PostMapping("/{fishingTripPostId}/comment")
	public ResponseEntity<GenericResponse<Void>> createComment(
		@Parameter(description = "댓글을 달 동출 게시글 ID", example = "1")
		@PathVariable final Long fishingTripPostId,
		@RequestBody @Valid final CommentRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long saveCommentId = commentService.createComment(fishingTripPostId, user.getId(), requestDto);

		return ResponseEntity.created(URI.create(saveCommentId.toString())).body(GenericResponse.of(true));
	}

	@Operation(summary = "댓글 조회", description = "댓글 조회시 사용하는 API")
	@GetMapping("/{fishingTripPostId}/comment")
	public ResponseEntity<GenericResponse<ScrollResponse<CommentResponse.Detail>>> getDetailList(
		@Parameter(description = "댓글을 조회할 동출 게시글 ID", example = "1")
		@PathVariable final Long fishingTripPostId,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long memberId = user.getId() == null ? -1 : user.getId();

		ScrollResponse<CommentResponse.Detail> getDetailList = commentService.getDetailList(
			fishingTripPostId,
			memberId,
			cursorRequestDto,
			requestDto
		);

		return ResponseEntity.ok(GenericResponse.of(true, getDetailList));
	}

	@Operation(summary = "댓글 수정", description = "댓글 수정시 사용하는 API")
	@PatchMapping("/{fishingTripPostId}/comment/{commentId}")
	public ResponseEntity<GenericResponse<Void>> udpateComment(
		@Parameter(description = "댓글이 달려있는 동출 게시글 ID", example = "1")
		@PathVariable final Long fishingTripPostId,
		@Parameter(description = "수정할 댓글 ID", example = "1")
		@PathVariable final Long commentId,
		@RequestBody @Valid final CommentRequest.Update requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		commentService.updateComment(user.getId(), commentId, fishingTripPostId, requestDto);

		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@Operation(summary = "댓글 삭제", description = "댓글 삭제시 사용하는 API (자식 댓글도 함께 삭제)")
	@DeleteMapping("/{fishingTripPostId}/comment/{commentId}")
	public ResponseEntity<GenericResponse<Void>> deleteComment(
		@Parameter(description = "댓글이 달려있는 동출 게시글 ID", example = "1")
		@PathVariable final Long fishingTripPostId,
		@Parameter(description = "삭제할 댓글 ID", example = "1")
		@PathVariable final Long commentId,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {
		commentService.deleteComment( user.getId(), commentId, fishingTripPostId);

		return ResponseEntity.ok(GenericResponse.of(true));
	}
}

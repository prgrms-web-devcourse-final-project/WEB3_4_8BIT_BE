package com.backend.domain.comment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.service.CommentService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fishing-trip-post")
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/{fishingTripPostId}/comment")
	@Parameter
	public ResponseEntity<GenericResponse<Void>> createComment(
		@Parameter(description = "댓글을 달 동출 게시글 ID", example = "1")
		@PathVariable final Long fishingTripPostId,
		@RequestBody @Valid final CommentRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long saveCommentId = commentService.createComment(fishingTripPostId, user.getId(), requestDto);

		return ResponseEntity.created(URI.create(saveCommentId.toString())).body(GenericResponse.of(true));
	}
}

package com.backend.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.service.ReviewService;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "review", description = "리뷰 API")
@RequestMapping("/api/v1/ship-fish-posts/{id}/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@Operation(summary = "리뷰 작성", description = "리뷰 작성 API")
	public ResponseEntity<GenericResponse<Long>> createReview(
		@PathVariable Long id,
		@RequestBody @Valid ReviewRequest.Create request
	) {
		// TODO 추후에 리뷰 ID를 리턴할지 게시글 ID를 리턴할지 결정되면 수정
		return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.ok(reviewService.createReview(id, request)));
	}
}

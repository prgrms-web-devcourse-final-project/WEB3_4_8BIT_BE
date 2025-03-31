package com.backend.domain.review.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.dto.response.ScrollResponse;
import com.backend.domain.review.service.ReviewService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "선상 낚시 리뷰 API")
@RequestMapping("/api/v1/")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reservations/{reservationId}/reviews")
	@Operation(summary = "선상 낚시 리뷰 생성", description = "내 예약에서 선상 낚시에 대한 리뷰 작성할 때 사용하는 API")
	public ResponseEntity<GenericResponse<Void>> createReview(
		@PathVariable final Long reservationId,
		@RequestBody @Valid final ReviewRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {
		Long savedReviewId = reviewService.save(user.getId(), reservationId, requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(GenericResponse.of(true));
	}

	@GetMapping("/ship-posts/{postId}/reviews")
	@Operation(summary = "선상 낚시 리뷰 조회", description = "게시글 ID로 리뷰를 조회하는 API")
	public ResponseEntity<GenericResponse<ScrollResponse<ReviewWithMemberResponse>>> getReviewsByPostId(
		@PathVariable final Long postId,
		@PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
	) {
		ScrollResponse<ReviewWithMemberResponse> scrollResponse = ScrollResponse.from(
			reviewService.getReviewListByPostId(postId, pageable));
		return ResponseEntity.status(HttpStatus.OK)
			.body(GenericResponse.of(true, scrollResponse));
	}

	@GetMapping("/members/{memberId}/reviews")
	@Operation(summary = "내가 작성한 리뷰 조회", description = "회원 ID로 리뷰를 조회하는 API")
	public ResponseEntity<GenericResponse<ScrollResponse<ReviewWithMemberResponse>>> getReviewsByMemberId(
		@PathVariable final Long memberId,
		@PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable
	) {
		ScrollResponse<ReviewWithMemberResponse> scrollResponse = ScrollResponse.from(
			reviewService.getReviewListByMemberId(memberId, pageable));
		return ResponseEntity.status(HttpStatus.OK)
			.body(GenericResponse.of(true, scrollResponse));
	}
}

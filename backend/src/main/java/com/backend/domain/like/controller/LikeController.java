package com.backend.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.service.LikeService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name = "좋아요 API", description = "좋아요 토글 관련 API")
public class LikeController {

	private final LikeService likeService;

	@Operation(summary = "좋아요 토글", description = "좋아요가 없으면 생성, 있으면 소프트 딜리트, 다시 클릭시 복구 합니다.")
	@PostMapping("/toggle")
	public ResponseEntity<GenericResponse<Void>> toggleLike(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid @RequestBody final LikeRequest requestDto
	) {
		likeService.toggleLike(user.getId(), requestDto);
		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@GetMapping("/fishing-trip-post")
	@Operation(summary = "좋아요한 동출 모집 게시글 스크롤 조회", description = "로그인한 사용자가 좋아요한 동출 모집 게시글을 최신순으로 조회합니다.")
	public ResponseEntity<GenericResponse<ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse>>> getLikedFishingTripPosts(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse> responseDto = likeService.getLikedFishingTripPosts(
			cursorRequestDto,
			user.getId()
		);
		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@GetMapping("/ship-fishing-post")
	@Operation(summary = "좋아요한 선상 낚시 게시글 스크롤 조회", description = "로그인한 사용자가 좋아요한 동출 모집 게시글을 최신순으로 조회합니다.")
	public ResponseEntity<GenericResponse<ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse>>> getLikedShipFishingPosts(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse> responseDto = likeService.getLikedShipFishingPosts(
			cursorRequestDto,
			user.getId()
		);
		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}
}

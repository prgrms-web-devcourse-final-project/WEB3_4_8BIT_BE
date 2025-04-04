package com.backend.domain.fishingtrippost.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.service.FishingTripPostService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "동출 모집 API")
@RequestMapping("/api/v1/fishing-trip-post")
public class FishingTripPostController {

	private final FishingTripPostService fishingTripPostService;

	@PostMapping
	@Operation(summary = "동출 모집 게시글 생성", description = "로그인한 사용자가 동출 모집 게시글 작성시 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> createFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestBody @Valid final FishingTripPostRequest.Form requestDto
	) {

		Long saveFishingTripPostId = fishingTripPostService.createFishingTripPost(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(saveFishingTripPostId.toString()))
			.body(GenericResponse.of(true));
	}

	@PatchMapping("/{fishingTripPostId}")
	@Operation(summary = "동출 모집 게시글 수정", description = "로그인한 사용자가 동출 모집 게시글 수정시 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> updateFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@PathVariable final Long fishingTripPostId,
		@RequestBody @Valid final FishingTripPostRequest.Form requestDto
	) {

		Long updateFishingTripPostId = fishingTripPostService.updateFishingTripPost(
			user.getId(),
			fishingTripPostId,
			requestDto
		);

		return ResponseEntity.ok(GenericResponse.of(true, updateFishingTripPostId));
	}
}

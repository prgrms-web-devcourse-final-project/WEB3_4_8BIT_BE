package com.backend.domain.fishingtriprecruitment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.service.FishingTripRecruitmentService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "동출 모집 신청 API")
@RequestMapping("/api/v1/fishing-trip-recruitment")
public class FishingTripRecruitmentController {

	private final FishingTripRecruitmentService fishingTripRecruitmentService;

	@PostMapping
	@Operation(summary = "동출 모집 신청", description = "로그인한 사용자가 동출 모집 게시글에서 신청하는 API")
	public ResponseEntity<GenericResponse<Long>> createFishingTripRecruitment(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestBody @Valid final FishingTripRecruitmentRequest.Create requestDto
	) {

		Long fishingTripRecruitmentId = fishingTripRecruitmentService.createFishingTripRecruitment(
			user.getId(),
			requestDto
		);

		return ResponseEntity.created(URI.create(fishingTripRecruitmentId.toString()))
			.body(GenericResponse.of(true));
	}
}

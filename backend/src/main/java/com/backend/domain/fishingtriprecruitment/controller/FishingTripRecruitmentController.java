package com.backend.domain.fishingtriprecruitment.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.service.FishingTripRecruitmentService;
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

	@PatchMapping("/{fishingTripRecruitmentId}/refuse")
	@Parameter(name = "fishingTripRecruitmentId", required = true, description = "동출모집 신청 ID", example = "1")
	@Operation(summary = "동출 모집 거절", description = "동출모집 게시글 작성자가 동출모집 신청에 대해서 거절을 하는 API")
	public ResponseEntity<GenericResponse<Void>> refuseFishingTripRecruitment(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@PathVariable final Long fishingTripRecruitmentId
	) {
		fishingTripRecruitmentService.refuseFishingTripRecruitment(user.getId(), fishingTripRecruitmentId);
		return ResponseEntity.ok().body(GenericResponse.of(true));
	}

	@GetMapping("/participants")
	@Operation(summary = "동출 모집 신청자 조회", description = "동출 모집 게시글 작성자가 신청자 목록을 페이징 조회하는 API")
	@Parameter(name = "fishingTripPostId", required = true, description = "동출모집 게시글 ID", example = "1")
	@Parameter(name = "status", required = true, description = "동출모집 상태 ", example = "PENDING")
	public ResponseEntity<GenericResponse<ScrollResponse<FishingTripRecruitmentResponse.DetailPage>>> getFishingTripRecruitmentDetailPageList(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestParam final Long fishingTripPostId,
		@RequestParam final RecruitmentStatus status,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto
	) {
		ScrollResponse<FishingTripRecruitmentResponse.DetailPage> detailPageList =
			fishingTripRecruitmentService.getDetailPageList(user.getId(), cursorRequestDto, fishingTripPostId, status);

		return ResponseEntity.ok(GenericResponse.of(true, detailPageList));
	}
}

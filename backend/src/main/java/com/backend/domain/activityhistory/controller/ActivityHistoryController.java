package com.backend.domain.activityhistory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.service.ActivityHistoryService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity-histories")
@Tag(name = "최근 활동 내역 API")
public class ActivityHistoryController {

	private final ActivityHistoryService activityHistoryService;

	@GetMapping
	@Operation(summary = "최근 활동 내역 조회", description = "최근 활동 내역 조회시 사용하는 API")
	public ResponseEntity<GenericResponse<ScrollResponse<ActivityHistoryResponse.Detail>>> getDetailList(
		@Valid final ActivityHistoryRequest.Search requestDto,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {
		ScrollResponse<ActivityHistoryResponse.Detail> detailList = activityHistoryService.getDetailList(
			cursorRequestDto, requestDto, user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, detailList));
	}
}

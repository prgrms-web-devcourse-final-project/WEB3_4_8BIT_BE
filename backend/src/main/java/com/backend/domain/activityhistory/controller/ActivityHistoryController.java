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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity-histories")
public class ActivityHistoryController {

	private final ActivityHistoryService activityHistoryService;

	@GetMapping
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

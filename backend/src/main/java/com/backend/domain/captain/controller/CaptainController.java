package com.backend.domain.captain.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.service.CaptainService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "선장 API")
@RequestMapping("/api/v1/members/captains")
public class CaptainController {

	private final CaptainService captainService;

	@PostMapping
	@Operation(summary = "선장 선택시 추가정보 받고 선장 생성", description = "추가정보를 받고 선장 생성할때 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> createCaptain(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestBody @Valid final CaptainRequest.Create requestDto
	) {

		Long saveCaptainId = captainService.createCaptain(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(saveCaptainId.toString()))
			.body(GenericResponse.of(true));
	}

	@GetMapping
	@Operation(summary = "선장 정보 조회", description = "현재 로그인된 선장 정보를 조회하는 API")
	public ResponseEntity<GenericResponse<CaptainResponse.Detail>> getCaptainDetail(
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		CaptainResponse.Detail responseDto = captainService.getCaptainDetail(user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@PatchMapping
	@Operation(summary = "선장 배 정보 수정", description = "현재 로그인된 선장의 배 정보를 수정하는 API")
	public ResponseEntity<GenericResponse<Void>> updateCaptain(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestBody @Valid final CaptainRequest.Update requestDto
	) {

		Long captainId = captainService.updateCaptainShipList(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(captainId.toString())).body(GenericResponse.of(true));
	}
}

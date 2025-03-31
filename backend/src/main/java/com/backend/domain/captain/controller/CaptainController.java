package com.backend.domain.captain.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.service.CaptainService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.response.GenericResponse;

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
}

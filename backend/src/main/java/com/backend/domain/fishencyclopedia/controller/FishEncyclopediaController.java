package com.backend.domain.fishencyclopedia.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediaService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fish/encyclopedias")
@RequiredArgsConstructor
@Tag(name = "물고기 도감 API")
public class FishEncyclopediaController {

	private final FishEncyclopediaService fishEncyclopediaService;

	@Operation(summary = "물고기 도감 추가하기", description = "물고기 도감 추가시 사용하는 API")
	@PostMapping
	public ResponseEntity<GenericResponse<Void>> createFishEncyclopedia(
		@RequestBody @Valid final FishEncyclopediaRequest.Create create,
		@AuthenticationPrincipal final CustomOAuth2User customOAuth2User
	) {

		Long savedFishEncyclopediaId = fishEncyclopediaService.createFishEncyclopedia(create, customOAuth2User.getId());

		return ResponseEntity
			.created(URI.create(savedFishEncyclopediaId.toString()))
			.body(GenericResponse.of(true));
	}
}
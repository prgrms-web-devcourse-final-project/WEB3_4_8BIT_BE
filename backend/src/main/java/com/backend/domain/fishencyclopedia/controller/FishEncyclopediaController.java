package com.backend.domain.fishencyclopedia.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediaService;
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
@RequestMapping("/api/v1/fishes")
@RequiredArgsConstructor
@Tag(name = "물고기 도감 API")
public class FishEncyclopediaController {

	private final FishEncyclopediaService fishEncyclopediaService;

	//TODO 프론트랑 얘기해서 request 객체를 어떻게 받을 것인지 정해야함
	@Operation(summary = "물고기 도감 추가하기", description = "물고기 도감 추가시 사용하는 API")
	@PostMapping("/encyclopedias")
	public ResponseEntity<GenericResponse<Void>> createFishEncyclopedia(
		@RequestBody @Valid final FishEncyclopediaRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long savedFishEncyclopediaId = fishEncyclopediaService.createFishEncyclopedia(requestDto, user.getId());

		return ResponseEntity
			.created(URI.create(savedFishEncyclopediaId.toString()))
			.body(GenericResponse.of(true));
	}

	@Operation(summary = "물고기 도감 상세 조회", description = "물고기 도감 상세 조회시 사용하는 API")
	@GetMapping("/{fishId}/encyclopedias")
	public ResponseEntity<GenericResponse<ScrollResponse<FishEncyclopediaResponse.Detail>>> getDetailList(
		@Parameter(description = "조회할 물고기의 ID", example = "1")
		@PathVariable final Long fishId,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		ScrollResponse<FishEncyclopediaResponse.Detail> detailList = fishEncyclopediaService.getDetailList(
			cursorRequestDto,
			fishId,
			user.getId()
		);

		return ResponseEntity.ok(GenericResponse.of(true, detailList));
	}

	@Operation(summary = "물고기 도감 상세 조회", description = "물고기 도감 상세 조회시 사용하는 API")
	@GetMapping("/encyclopedias")
	public ResponseEntity<GenericResponse<List<FishEncyclopediaResponse.DetailPage>>> getDetailPageList(
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		List<FishEncyclopediaResponse.DetailPage> detailPageList = fishEncyclopediaService.getDetailPageList(
			user.getId()
		);

		return ResponseEntity.ok(GenericResponse.of(true, detailPageList));
	}
}
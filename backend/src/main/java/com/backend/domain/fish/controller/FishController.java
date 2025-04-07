package com.backend.domain.fish.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fish.dto.FishRequest;
import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.service.FishService;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fishes")
@RequiredArgsConstructor
@Tag(name = "물고기 API")
public class FishController {

	private final FishService fishService;

	@Operation(summary = "물고기 조회", description = "물고기 조회시 사용하는 API")
	@GetMapping("/{fishId}")
	public ResponseEntity<GenericResponse<FishResponse.Detail>> getDetail(@PathVariable final Long fishId) {

		FishResponse.Detail getDetail = fishService.getFishDetail(fishId);

		return ResponseEntity.ok(GenericResponse.of(true, getDetail));
	}

	@Operation(summary = "물고기 인기순 조회", description = "물고기 인기순 조회시 사용하는 API")
	@GetMapping("/popular")
	public ResponseEntity<GenericResponse<List<FishResponse.Popular>>> getDetail(
		@Valid final FishRequest.Popular requestDto
	) {

		List<FishResponse.Popular> getPopularList = fishService.getPopular(requestDto.size());

		return ResponseEntity.ok(GenericResponse.of(true, getPopularList));
	}
}

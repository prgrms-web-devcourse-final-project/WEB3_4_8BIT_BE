package com.backend.domain.fishpoint.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.service.FishPointService;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "낚시 포인트 API")
@RequestMapping("/api/v1/fish-points")
public class FishPointController {

	private final FishPointService fishPointService;

	@GetMapping("/bounds")
	@Operation(summary = "지도 내 낚시 포인트 조회", description = "지도 범위 내에 있는 낚시 포인트 조회 API")
	public ResponseEntity<GenericResponse<List<FishPointResponse>>> getFishPointByBounds(
		@RequestParam final double swLat,
		@RequestParam final double swLng,
		@RequestParam final double neLat,
		@RequestParam final double neLng
	) {
		List<FishPointResponse> fishPointList = fishPointService.getFishPointsByBounds(swLat, swLng, neLat, neLng);
		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping
	@Operation(summary = "낚시 포인트 검색", description = "지역명을 기준으로 낚시 포인트 검색 API")
	public ResponseEntity<GenericResponse<List<FishPointResponse>>> getFishPointBySearch(
		@RequestParam(name = "region") final String fishPointName
	) {
		List<FishPointResponse> fishPointList = fishPointService.searchFishPoints(fishPointName);
		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}
}

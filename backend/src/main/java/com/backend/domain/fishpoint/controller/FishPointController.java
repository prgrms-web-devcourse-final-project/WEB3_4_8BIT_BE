package com.backend.domain.fishpoint.controller;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishpoint.dto.request.FishPointRequest;
import com.backend.domain.fishpoint.service.FishPointService;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "낚시 포인트 API")
@RequestMapping("/api/v1/fish-points")
public class FishPointController {

	private final FishPointService fishPointService;

	@GetMapping("/bounds")
	@Operation(summary = "지도 내 낚시 포인트 조회", description = "지도 범위 내에 있는 낚시 포인트를 조회하는 API")
	public ResponseEntity<GenericResponse<List<Basic>>> getFishPointByBounds(
		@Valid @ModelAttribute final FishPointRequest.Bounds boundsRequestDto
	) {
		List<Basic> fishPointList = fishPointService.getFishPointsByBounds(
			boundsRequestDto.swLat(),
			boundsRequestDto.swLng(),
			boundsRequestDto.neLat(),
			boundsRequestDto.neLng()
		);

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/nearby")
	@Operation(
		summary = "내 위치 근처 낚시 포인트 조회",
		description = "사용자의 현재 위치를 기준으로 설정한 반경(km) 내에 있는 낚시 포인트 목록을 조회하는 API"
	)
	public ResponseEntity<GenericResponse<List<WithDistance>>> getNearbyFishPoints(
		@Valid @ModelAttribute final FishPointRequest.Nearby nearbyRequestDto
	) {
		List<WithDistance> fishPointList = fishPointService.getNearbyFishPoints(
			nearbyRequestDto.lat(),
			nearbyRequestDto.lng(),
			nearbyRequestDto.radiusKm()
		);

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/nearest")
	@Operation(
		summary = "내 위치 근처 낚시 포인트 조회",
		description = "사용자의 현재 위치를 기준으로 가장 가까이 있는 낚시 포인트를 3개 조회하는 API"
	)
	public ResponseEntity<GenericResponse<List<WithDistance>>> getNearestFishPoints(
		@Valid @ModelAttribute final FishPointRequest.Nearby nearbyRequestDto
	) {
		List<WithDistance> fishPointList = fishPointService.getNearestFishPoints(
			nearbyRequestDto.lat(),
			nearbyRequestDto.lng()
		);

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/regions/{regionId}")
	@Operation(summary = "지역 기반 낚시 포인트 조회", description = "지역 ID(도 단위) 기준 낚시 포인트를 조회하는 API")
	public ResponseEntity<GenericResponse<List<Basic>>> getRegionFishPoints(
		@PathVariable final Long regionId
	) {
		List<Basic> fishPointList = fishPointService.getFishPointsByRegionId(regionId);

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/search")
	@Operation(summary = "낚시 포인트 검색", description = "지역명을 기준으로 낚시 포인트 검색 API")
	public ResponseEntity<GenericResponse<List<Basic>>> getFishPointBySearch(
		@Valid @ModelAttribute final FishPointRequest.Search searchRequestDto
	) {
		List<Basic> fishPointList = fishPointService.searchFishPoints(searchRequestDto.region());

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/popular")
	@Operation(summary = "인기 낚시 포인트 조회", description = "동출 게시글 기준으로 인기 낚시 포인트를 조회하는 API")
	public ResponseEntity<GenericResponse<List<Popularity>>> getPopularFishPoints() {
		List<Popularity> fishPointList = fishPointService.getPopularityFishPoints();

		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}

	@GetMapping("/{fishPointId}")
	@Operation(summary = "낚시 포인트 상세 조회", description = "낚시 포인트의 상세 정보를 조회하는 API")
	public ResponseEntity<GenericResponse<Detail>> getFishPointDetail(@PathVariable final Long fishPointId) {
		Detail fishPointDetail = fishPointService.getFishPointDetail(fishPointId);

		return ResponseEntity.ok(GenericResponse.of(true, fishPointDetail));
	}
}

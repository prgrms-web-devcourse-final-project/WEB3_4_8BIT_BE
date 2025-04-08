package com.backend.domain.region.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.region.dto.response.RegionResponse;
import com.backend.domain.region.service.RegionService;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "지역 API")
@RequestMapping("/api/v1/regions")
public class RegionController {

	private final RegionService regionService;

	@GetMapping("/regions")
	@Operation(summary = "지역 정보 조회", description = "지역 정보(도 단위)를 조회하는 API")
	public ResponseEntity<GenericResponse<List<RegionResponse.Basic>>> getAllRegions() {

		List<RegionResponse.Basic> regionList = regionService.getAllRegions();

		return ResponseEntity.ok(GenericResponse.of(true, regionList));
	}
}

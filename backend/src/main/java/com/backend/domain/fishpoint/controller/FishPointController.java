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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fish-points")
public class FishPointController {

	private final FishPointService fishPointService;

	@GetMapping("/bounds")
	public ResponseEntity<GenericResponse<List<FishPointResponse>>> getFishPointByBounds(
		final @RequestParam double swLat,
		final @RequestParam double swLng,
		final @RequestParam double neLat,
		final @RequestParam double neLng
	) {
		List<FishPointResponse> fishPointList = fishPointService.getFishPointsByBounds(swLat, swLng, neLat, neLng);
		return ResponseEntity.ok(GenericResponse.of(true, fishPointList));
	}
}

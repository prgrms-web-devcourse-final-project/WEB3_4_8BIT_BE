package com.backend.domain.ship.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.service.ShipService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ship")
@RequiredArgsConstructor
@Tag(name = "선박 API")
public class ShipController {

	private final ShipService shipService;

	@Operation(summary = "선박 추가하기", description = "선박 추가시 사용하는 API")
	@PostMapping
	public ResponseEntity<GenericResponse<Void>> createShip(
		@RequestBody @Valid final ShipRequest.Form requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long savedShipId = shipService.createShip(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(savedShipId.toString())).body(GenericResponse.of(true));
	}

	@Operation(summary = "로그인한 회원 선박 전체 조회", description = "로그인한 회원의 선박을 전체 조회 할 때 사용하는 API")
	@GetMapping
	public ResponseEntity<GenericResponse<List<ShipResponse.Detail>>> getDetailAll(
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		List<ShipResponse.Detail> getShipAllList = shipService.getDetailAll(user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, getShipAllList));
	}

	@Operation(summary = "선박 수정하기", description = "선박 수정할 때 사용하는 API")
	@PatchMapping("/{shipId}")
	public ResponseEntity<GenericResponse<Long>> updateShip(
		@PathVariable final Long shipId,
		@RequestBody @Valid final ShipRequest.Form requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long updatedShipId = shipService.updateShip(shipId, user.getId(), requestDto);

		return ResponseEntity.ok(GenericResponse.of(true, updatedShipId));
	}
}

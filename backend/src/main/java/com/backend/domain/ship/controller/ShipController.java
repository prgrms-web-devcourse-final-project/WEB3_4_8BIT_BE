package com.backend.domain.ship.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.ship.dto.request.ShipRequest;
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
		@RequestBody @Valid final ShipRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long savedShipId = shipService.createShip(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(savedShipId.toString())).body(GenericResponse.of(true));
	}
}

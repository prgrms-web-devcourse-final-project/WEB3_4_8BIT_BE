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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ship")
@RequiredArgsConstructor
public class ShipController {

	private final ShipService shipService;

	@PostMapping
	public ResponseEntity<GenericResponse<Void>> save(
		@RequestBody @Valid final ShipRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long savedShipId = shipService.createShip(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(savedShipId.toString())).body(GenericResponse.of(true));
	}
}

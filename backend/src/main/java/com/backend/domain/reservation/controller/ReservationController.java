package com.backend.domain.reservation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.service.ReservationService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "예약 정보 API")
@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	@Operation(summary = "예약 신청 및 생성", description = "유저가 선상 낚시를 예약 할 때 사용하는 API")
	public ResponseEntity<GenericResponse<ReservationResponse.Detail>> saveReservation(
		@RequestBody @Valid final ReservationRequest.Reserve requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user) {

		ReservationResponse.Detail response = reservationService.createReservation(requestDto, user.getId());

		return ResponseEntity.created(URI.create(response.reservationId().toString()))
			.body(GenericResponse.of(true, response));
	}

}

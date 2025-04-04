package com.backend.domain.reservation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

	@GetMapping("/{id}")
	@Operation(summary = "예약 내역 상세 조회", description = "유저가 선상 낚시를 예약 정보를 상세 조회 할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "예약 Id", example = "1")
	public ResponseEntity<GenericResponse<ReservationResponse.DetailWithMemberName>> getReservation(
		@PathVariable("id") @Min(1) final Long reservationId) {

		ReservationResponse.DetailWithMemberName response = reservationService.getReservation(reservationId);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}
}

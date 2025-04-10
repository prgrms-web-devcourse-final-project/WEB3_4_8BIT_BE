package com.backend.domain.reservation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.service.ReservationService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "예약 정보 API")
@RestController
@RequestMapping("/api/v1/reservations")
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
	@Operation(summary = "예약 내역 상세 조회", description = "유저가 선상 낚시 예약 정보를 상세 조회 할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "예약 Id", example = "1")
	public ResponseEntity<GenericResponse<ReservationResponse.DetailWithMember>> getReservation(
		@PathVariable("id") final Long reservationId,
		@AuthenticationPrincipal final CustomOAuth2User user) {

		ReservationResponse.DetailWithMember response = reservationService.getReservation(reservationId, user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/count")
	@Operation(summary = "예약 내역 횟수 조회", description = "유저가 선상 낚시 예약 횟수를 조회 할 때 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> getReservationCount(
		@AuthenticationPrincipal final CustomOAuth2User user) {

		Long response = reservationService.getReservationCount(user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/members")
	@Operation(summary = "예약 내역 조회 (유저)", description = "유저가 본인이 예약한 내역을 조회 할 때 사용하는 API")
	@Parameter(name = "afterToday", description = "오늘 이후 예약인지, 이전 예약인지 여부", example = "true")
	@Parameter(name = "isConfirm", description = "확정된 예약인지, 취소된 예약인지 여부", example = "true")
	public ResponseEntity<GenericResponse<ScrollResponse<ReservationResponse.DetailReservationList>>> getUserReservationList(
		@RequestParam final Boolean afterToday,
		@RequestParam final Boolean isConfirm,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		ScrollResponse<ReservationResponse.DetailReservationList> response = reservationService
			.getUserReservationListWithImage(user.getId(), afterToday, isConfirm, cursorRequestDto);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/captains")
	@Operation(summary = "예약 내역 조회 (선장)", description = "선장이 예약 리스트를 조회 할 때 사용하는 API")
	@Parameter(name = "shipFishingPostId", description = "선상 낚시 게시글 ID", example = "1")
	@Parameter(name = "afterToday", description = "오늘 이후 예약인지, 이전 예약인지 여부", example = "true")
	public ResponseEntity<GenericResponse<ScrollResponse<ReservationResponse.DetailWithName>>> getCaptainReservationList(
		@RequestParam(required = false) final Long shipFishingPostId,
		@RequestParam final Boolean afterToday,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		ScrollResponse<ReservationResponse.DetailWithName> response = reservationService
			.getCaptainReservationList(shipFishingPostId, user.getId(), afterToday, cursorRequestDto);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/dashboard")
	@Operation(summary = "선장 마이페이지 대시보드", description = "선장의 마이페이지에서 대시보드 내용을(새 예약 신청, 다가오는 예약, 작성한 게시글 수) 조회 할 때 사용하는 API")
	public ResponseEntity<GenericResponse<ReservationResponse.DashBoard>> getReservationDashBoard(
		@RequestParam(value = "limitDays", required = false, defaultValue = "5") @Min(1) final Integer limitDays,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		ReservationResponse.DashBoard response = reservationService.getDashBoard(user.getId(), limitDays);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@PatchMapping("/{id}")
	@Operation(summary = "예약 취소", description = "유저가 선상 낚시 예약을 취소 할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "예약 Id", example = "1")
	public ResponseEntity<GenericResponse<ReservationResponse.DetailWithMember>> updateReservation(
		@PathVariable("id") final Long reservationId,
		@AuthenticationPrincipal final CustomOAuth2User user) {

		reservationService.updateReservation(reservationId, user.getId());

		return ResponseEntity.ok(GenericResponse.of(true));
	}
}

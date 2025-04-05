package com.backend.domain.reservationdate.Controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.reservationdate.dto.response.ReservationDateResponse;
import com.backend.domain.reservationdate.service.ReservationDateService;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "예약 일자 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation_dates")
public class ReservationDateController {

	private final ReservationDateService reservationDateService;

	@GetMapping("/{id}/remain")
	@Operation(summary = "예약 가능 인원 조회", description = "유저가 선상 낚시 잔여 인원를 확인하기 위한 API")
	@Parameter(name = "id", required = true, description = "선상 낚시 게시글 ID", example = "1")
	@Parameter(name = "reservationDate", required = true, description = "조회할 예약 일자", example = "2025-04-02")
	public ResponseEntity<GenericResponse<ReservationDateResponse.Detail>> getReservationDate(
		@PathVariable("id") @Min(1) final Long shipFishingPostId,
		@RequestParam final LocalDate reservationDate
	) {

		ReservationDateResponse.Detail response = reservationDateService
			.getReservationDate(shipFishingPostId, reservationDate);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/{id}/dates")
	@Operation(summary = "예약 일자 조회", description = "유저가 선상 낚시 예약 가능 여부를 확인하기 위한 API")
	@Parameter(name = "id", required = true, description = "선상 낚시 게시글 ID", example = "1")
	@Parameter(name = "reservationDate", required = true, description = "조회할 예약 일자", example = "2025-04-02")
	public ResponseEntity<GenericResponse<ReservationDateResponse.UnAvailableDateList>> getAvailableReservationDateList(
		@PathVariable("id") @Min(1) final Long shipFishingPostId,
		@RequestParam final LocalDate reservationDate
	) {

		ReservationDateResponse.UnAvailableDateList response = reservationDateService
			.getReservationDateAvailableList(shipFishingPostId, reservationDate);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

}

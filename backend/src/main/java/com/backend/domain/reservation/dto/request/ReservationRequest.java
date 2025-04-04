package com.backend.domain.reservation.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

public class ReservationRequest {

	/**
	 * {
	 *     "shipFishingPostId": 1,
	 *     "guestCount": 2,
	 *     "price": 80000,
	 *     "totalPrice": 160000,
	 *     "reservationDate": "2025-04-02"
	 * }
	 *
	 * @param shipFishingPostId
	 * @param guestCount
	 * @param price
	 * @param totalPrice
	 * @param reservationDate
	 */
	@Builder
	public record Reserve(
		@NotNull(message = "선상 낚시 게시글 id 는 필수 항목입니다.") @Schema(description = "선상 낚시 게시글 id", example = "1") Long shipFishingPostId,

		@Min(value = 1, message = "예약 인원은 최소 1명 이상 입니다.") @Schema(description = "예약 인원", example = "2") Integer guestCount,

		@Positive(message = "금액은 0 이상이어야 합니다.") @Digits(integer = 6, fraction = 0, message = "금액은 최대 6자리(정수 6자리)까지 허용됩니다.") @Schema(description = "프로그램 1인 금액", example = "80000") Long price,

		@Positive(message = "금액은 0 이상이어야 합니다.") @Digits(integer = 6, fraction = 0, message = "금액은 최대 6자리(정수 6자리)까지 허용됩니다.") @Schema(description = "프로그램 총 금액", example = "160000") Long totalPrice,

		@NotNull(message = "예약 일자는 필수 항목입니다.") @Schema(description = "예약 일자", example = "2025-04-02") LocalDate reservationDate) {
	}
}

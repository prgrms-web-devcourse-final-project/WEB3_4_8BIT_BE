package com.backend.domain.ship.dto.request;

import com.backend.domain.ship.domain.RestroomType;
import com.backend.global.validator.ValidEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShipRequest {

	/**
	 * {
	 * "shipName": "해랑호",
	 * "shipNumber": "123456",
	 * "departurePort": "동해항",
	 * "passengerCapacity": 15,
	 * "restroomType": "COMMON",
	 * "loungeArea": true,
	 * "kitchenFacility": true,
	 * "fishingChair": true,
	 * "passengerInsurance": true,
	 * "fishingGearRental": true,
	 * "mealProvided": true,
	 * "parkingAvailable": true
	 * }
	 *
	 * @param shipName           선박 이름
	 * @param shipNumber         선박 번호
	 * @param departurePort      출항지
	 * @param passengerCapacity  승객 정원
	 * @param restroomType       화장실 유형
	 * @param loungeArea         휴게 공간 여부
	 * @param kitchenFacility    조리 시설 여부
	 * @param fishingChair       낚시 의자 여부
	 * @param passengerInsurance 승객 보험 여부
	 * @param fishingGearRental  장비 대여 여부
	 * @param mealProvided       식사 제공 여부
	 * @param parkingAvailable   주차 여부
	 * @author Kim Dong O
	 */
	public record Create(
		@Size(max = 30, message = "선박 이름은 30자 이하여야 합니다.")
		@NotBlank(message = "선박 이름은 필수 항목입니다,")
		@Schema(description = "선박 이름", example = "해랑호")
		String shipName,

		@Size(max = 30, message = "선박 번호는 30자 이하여야 합니다.")
		@NotBlank(message = "선박 번호는 필수 항목입니다,")
		@Schema(description = "선박 번호", example = "123456")
		String shipNumber,

		@Size(max = 30, message = "선박 이름은 30자 이하여야 합니다.")
		@NotBlank(message = "선박 이름은 필수 항목입니다,")
		@Schema(description = "출항지", example = "동해항")
		String departurePort,

		@Min(value = 1, message = "선박 정원은 1명 이상이어야 합니다.")
		@Max(value = 100, message = "선박 정원은 100명 이하여야 합니다.")
		@NotNull(message = "선박 정원은 필수 항목입니다.")
		@Schema(description = "승객 정원", example = "15")
		Integer passengerCapacity,

		@ValidEnum(enumClass = RestroomType.class)
		@Schema(description = "화장실 유형", example = "PUBLIC")
		RestroomType restroomType,

		@NotNull(message = "휴게 공간 여부는 필수 항목입니다.")
		@Schema(description = "휴게 공간 여부", example = "true")
		Boolean loungeArea,

		@NotNull(message = "조리 시설 여부는 필수 항목입니다.")
		@Schema(description = "조리 시설 여부", example = "true")
		Boolean kitchenFacility,

		@NotNull(message = "낚시 의자 여부는 필수 항목입니다.")
		@Schema(description = "낚시 의자 여부", example = "true")
		Boolean fishingChair,

		@NotNull(message = "승객 보험 여부는 필수 항목입니다.")
		@Schema(description = "승객 보험 여부", example = "true")
		Boolean passengerInsurance,

		@NotNull(message = "장비 대여 여부는 필수 항목입니다.")
		@Schema(description = "장비 대여 여부", example = "true")
		Boolean fishingGearRental,

		@NotNull(message = "식사 제공 여부는 필수 항목입니다.")
		@Schema(description = "식사 제공 여부", example = "true")
		Boolean mealProvided,

		@NotNull(message = "주차 여부는 필수 항목입니다.")
		@Schema(description = "주차 여부", example = "true")
		Boolean parkingAvailable
	) {
	}
}

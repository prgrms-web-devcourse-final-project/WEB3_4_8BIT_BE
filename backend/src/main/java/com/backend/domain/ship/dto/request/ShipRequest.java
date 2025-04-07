package com.backend.domain.ship.dto.request;

public class ShipRequest {

	// TODO 화장실 ENUM으로 변경
	public record Create(
		String shipName,
		String shipNumber,
		String departurePort,
		Integer passengerCapacity,
		String restroomType,
		Boolean loungeArea,
		Boolean kitchenFacility,
		Boolean fishingChair,
		Boolean passengerInsurance,
		Boolean fishingGearRental,
		Boolean mealProvided,
		Boolean parkingAvailable
	) {

	}
}

package com.backend.domain.ship.dto.response;

public class ShipResponse {

	/**
	 *{
	 *   "shipId": 1,
	 * 	 "shipName": "나로호",
	 *   "shipNumber": "01234567890123",
	 *   "departurePort": "부산",
	 *   "publicRestroom": true,
	 *   "loungeArea": false,
	 *   "kitchenFacility": true,
	 *   "fishingChair": false,
	 *   "passengerInsurance": true,
	 *   "fishingGearRental": false,
	 *   "mealProvided": true,
	 *   "parkingAvailable": true
	 * }
	 *
	 * @param shipId 선박 id
	 * @param shipName 선박 이름
	 * @param shipNumber 선박 번호
	 * @param departurePort 선박 출항지
	 * @param publicRestroom 공용 화장실 여부
	 * @param loungeArea 휴게공간 여부
	 * @param kitchenFacility 조리시설 여부
	 * @param fishingChair 의제 제공 여부
	 * @param passengerInsurance 보험 여부
	 * @param fishingGearRental 장비 대여 여부
	 * @param mealProvided 식사 제공 여부
	 * @param parkingAvailable 주차 시설 여부
	 */
	public record Detail(
		Long shipId,
		String shipName,
		String shipNumber,
		String departurePort,
		Boolean publicRestroom,
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

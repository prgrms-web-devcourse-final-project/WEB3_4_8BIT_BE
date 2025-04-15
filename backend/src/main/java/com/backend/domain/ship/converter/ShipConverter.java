package com.backend.domain.ship.converter;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;

public class ShipConverter {

	public static Ship fromCreate(final Long memberId, final ShipRequest.Form requestDto) {
		return Ship.builder()
			.shipName(requestDto.shipName())
			.shipNumber(requestDto.shipNumber())
			.memberId(memberId)
			.departurePort(requestDto.departurePort())
			.portName(requestDto.portName())
			.passengerCapacity(requestDto.passengerCapacity())
			.restroomType(requestDto.restroomType())
			.loungeArea(requestDto.loungeArea())
			.kitchenFacility(requestDto.kitchenFacility())
			.fishingChair(requestDto.fishingChair())
			.passengerInsurance(requestDto.passengerInsurance())
			.fishingGearRental(requestDto.fishingGearRental())
			.mealProvided(requestDto.mealProvided())
			.parkingAvailable(requestDto.parkingAvailable())
			.build();
	}

	public static ShipResponse.Detail fromShip(final Ship ship) {

		return ShipResponse.Detail.builder()
			.shipId(ship.getShipId())
			.shipName(ship.getShipName())
			.shipNumber(ship.getShipNumber())
			.departurePort(ship.getDeparturePort())
			.restroomType(ship.getRestroomType())
			.loungeArea(ship.getLoungeArea())
			.kitchenFacility(ship.getKitchenFacility())
			.fishingChair(ship.getFishingChair())
			.passengerInsurance(ship.getPassengerInsurance())
			.fishingGearRental(ship.getFishingGearRental())
			.mealProvided(ship.getMealProvided())
			.parkingAvailable(ship.getParkingAvailable())
			.build();
	}
}

package com.backend.domain.ship.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class ShipServiceTest extends BaseTest {

	@Mock
	private ShipRepository shipRepository;

	@InjectMocks
	private ShipServiceImpl shipService;

	@Test
	@DisplayName("선박 저장 [Service] - Success")
	void t01() {
		// Given
		Long givenMemberId = 1L;
		ShipRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(ShipRequest.Create.class);

		ArbitraryBuilder<Ship> shipArbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipName", givenCreate.shipName())
			.set("shipNumber", givenCreate.shipNumber())
			.set("memberId", givenMemberId)
			.set("departurePort", givenCreate.departurePort())
			.set("passengerCapacity", givenCreate.passengerCapacity())
			.set("restroomType", givenCreate.restroomType())
			.set("loungeArea", givenCreate.loungeArea())
			.set("kitchenFacility", givenCreate.kitchenFacility())
			.set("fishingChair", givenCreate.fishingChair())
			.set("passengerInsurance", givenCreate.passengerInsurance())
			.set("fishingGearRental", givenCreate.fishingGearRental())
			.set("mealProvided", givenCreate.mealProvided())
			.set("parkingAvailable", givenCreate.parkingAvailable());

		Mockito.when(shipRepository.save(any(Ship.class))).thenReturn(
			shipArbitraryBuilder.set("shipId", 1L)
			.sample()
		);

		// When
		Long savedId = shipService.createShip(givenMemberId, givenCreate);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

}
package com.backend.domain.ship.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
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

		when(shipRepository.countByMemberId(givenMemberId)).thenReturn(3L);

		when(shipRepository.save(any(Ship.class))).thenReturn(
			shipArbitraryBuilder.set("shipId", 1L)
			.sample()
		);

		// When
		Long savedId = shipService.createShip(givenMemberId, givenCreate);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("선박 저장 [Service] - Success")
	void t02() {
		// Given
		Long givenMemberId = 1L;
		ShipRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(ShipRequest.Create.class);

		when(shipRepository.countByMemberId(givenMemberId)).thenReturn(6L);

		// When & Then
		assertThatThrownBy(() -> shipService.createShip(givenMemberId, givenCreate))
			.isInstanceOf(ShipException.class)
			.hasMessage(ShipErrorCode.MAX_SHIP_COUNT_EXCEEDED.getMessage());
	}

	@Test
	@DisplayName("회원 ID로 등록된 선박 전체 조회 [Service] - Success")
	void t03() {
		// Given
		Long givenMemberId = 1L;
		List<ShipResponse.Detail> givenShipAllList = fixtureMonkeyRecord.giveMeBuilder(ShipResponse.Detail.class)
			.sampleList(10);

		when(shipRepository.findDetailAll(givenMemberId)).thenReturn(givenShipAllList);

		// When
		List<ShipResponse.Detail> findShipAllList = shipService.getDetailAll(givenMemberId);

		// Then
		assertThat(findShipAllList).isEqualTo(givenShipAllList);
	}
}
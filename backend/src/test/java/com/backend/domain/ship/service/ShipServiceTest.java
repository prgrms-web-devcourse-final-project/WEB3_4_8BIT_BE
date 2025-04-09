package com.backend.domain.ship.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class ShipServiceTest extends BaseTest {

	@Mock
	private ShipRepository shipRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@InjectMocks
	private ShipServiceImpl shipService;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<Ship> shipArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Ship.class)
		.set("shipName", englishStringLength)
		.set("shipNumber", englishStringLength)
		.set("departurePort", "부산항");

	@Test
	@DisplayName("선박 저장 [Service] - Success")
	void t01() {
		// Given
		Long givenMemberId = 1L;
		ShipRequest.Form givenForm = fixtureMonkeyValidation.giveMeOne(ShipRequest.Form.class);

		ArbitraryBuilder<Ship> shipArbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipName", givenForm.shipName())
			.set("shipNumber", givenForm.shipNumber())
			.set("memberId", givenMemberId)
			.set("departurePort", givenForm.departurePort())
			.set("passengerCapacity", givenForm.passengerCapacity())
			.set("restroomType", givenForm.restroomType())
			.set("loungeArea", givenForm.loungeArea())
			.set("kitchenFacility", givenForm.kitchenFacility())
			.set("fishingChair", givenForm.fishingChair())
			.set("passengerInsurance", givenForm.passengerInsurance())
			.set("fishingGearRental", givenForm.fishingGearRental())
			.set("mealProvided", givenForm.mealProvided())
			.set("parkingAvailable", givenForm.parkingAvailable());

		when(shipRepository.countByMemberId(givenMemberId)).thenReturn(3L);

		when(shipRepository.save(any(Ship.class))).thenReturn(
			shipArbitraryBuilder.set("shipId", 1L)
				.sample()
		);

		// When
		Long savedId = shipService.createShip(givenMemberId, givenForm);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("선박 저장 [Service] - Success")
	void t02() {
		// Given
		Long givenMemberId = 1L;
		ShipRequest.Form givenForm = fixtureMonkeyValidation.giveMeOne(ShipRequest.Form.class);

		when(shipRepository.countByMemberId(givenMemberId)).thenReturn(6L);

		// When & Then
		assertThatThrownBy(() -> shipService.createShip(givenMemberId, givenForm))
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

	@Test
	@DisplayName("선박 수정 [Service] - Success")
	void t04() {
		// Given
		Long givenMemberId = 1L;

		ShipRequest.Form givenForm = fixtureMonkeyRecord.giveMeBuilder(ShipRequest.Form.class).sample();

		Ship givenShip = shipArbitraryBuilder
			.set("memberId", givenMemberId)
			.sample();

		when(shipRepository.findById(givenShip.getShipId())).thenReturn(Optional.ofNullable(givenShip));

		// When
		Long updatedShipId = shipService.updateShip(givenShip.getShipId(), givenMemberId, givenForm);

		// Then
		assertThat(updatedShipId).isEqualTo(givenShip.getShipId());
	}

	@Test
	@DisplayName("선박 수정 [Not Author] [Service] - Fail")
	void t05() {
		// Given
		Long givenMemberId = 1L;

		ShipRequest.Form givenForm = fixtureMonkeyRecord.giveMeBuilder(ShipRequest.Form.class).sample();

		Ship givenShip = shipArbitraryBuilder
			.set("memberId", 2L)
			.sample();

		when(shipRepository.findById(givenShip.getShipId())).thenReturn(Optional.ofNullable(givenShip));

		// When & Then
		assertThatThrownBy(() -> shipService.updateShip(givenShip.getShipId(), givenMemberId, givenForm))
			.isInstanceOf(ShipException.class)
			.hasMessage(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR.getMessage());
	}

	@Test
	@DisplayName("선박 삭제 [Service] - Success")
	void t06() {
		// Given
		Long givenMemberId = 1L;

		Ship givenShip = shipArbitraryBuilder
			.set("memberId", givenMemberId)
			.sample();

		when(shipRepository.findById(givenShip.getShipId())).thenReturn(Optional.ofNullable(givenShip));
		doNothing().when(shipRepository).deleteByShipId(givenShip.getShipId());
		when(shipFishingPostRepository.existsByShipId(givenShip.getShipId())).thenReturn(false);

		// When
		shipService.deleteById(givenShip.getShipId(), givenMemberId);

		// Then
		verify(shipRepository).deleteByShipId(givenShip.getShipId());
	}

	@Test
	@DisplayName("선박 삭제 [Not Author] [Service] - Fail")
	void t07() {
		// Given
		Long givenMemberId = 1L;
		Ship givenShip = shipArbitraryBuilder
			.set("memberId", 2L)
			.sample();
		when(shipRepository.findById(givenShip.getShipId())).thenReturn(Optional.ofNullable(givenShip));

		// When & Then
		assertThatThrownBy(() -> shipService.deleteById(givenShip.getShipId(), givenMemberId))
			.isInstanceOf(ShipException.class)
			.hasMessage(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR.getMessage());
	}

	@Test
	@DisplayName("선박 삭제 [In Use By Fishing Post] [Service] - Fail")
	void t08() {
		// Given
		Long givenMemberId = 1L;
		Ship givenShip = shipArbitraryBuilder
			.set("memberId", givenMemberId)
			.sample();
		when(shipRepository.findById(givenShip.getShipId())).thenReturn(Optional.ofNullable(givenShip));
		when(shipFishingPostRepository.existsByShipId(givenShip.getShipId())).thenReturn(true);

		// When & Then
		assertThatThrownBy(() -> shipService.deleteById(givenShip.getShipId(), givenMemberId))
			.isInstanceOf(ShipException.class)
			.hasMessage(ShipErrorCode.SHIP_IN_USE_BY_FISHING_POST.getMessage());
	}
}
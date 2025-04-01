package com.backend.domain.ship.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.ship.entity.Ship;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipRepositoryTest extends BaseTest {

	@Autowired
	private ShipRepository shipRepository;

	private final ArbitraryBuilder<Ship> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Ship.class)
		.set("shipName", "test name")
		.set("shipNumber", "01234567891011")
		.set("departurePort", "부산항");

	@Test
	@DisplayName("선박 정보 저장 [Repository] - Success")
	void t01() {
		// Given
		Ship givenShip = arbitraryBuilder.set("shipId", null).sample();

		// When
		Ship savedShip = shipRepository.save(givenShip);

		assertThat(savedShip).isNotNull();
		assertThat(savedShip.getShipId()).isNotNull();
	}

	@Test
	@DisplayName("선박 정보 조회 [Repository] - Success")
	void t02() {
		// Given
		Ship givenShip = arbitraryBuilder.set("shipId", null).sample();

		Ship savedShip = shipRepository.save(givenShip);
		Long savedShipId = savedShip.getShipId();

		// When
		Optional<Ship> savedOptionalShip = shipRepository.findById(savedShipId);

		// Then
		assertThat(savedOptionalShip).isPresent();
		assertThat(savedOptionalShip.get().equals(savedShip)).isTrue();
	}
}

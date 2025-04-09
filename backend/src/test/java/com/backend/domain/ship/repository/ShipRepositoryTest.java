package com.backend.domain.ship.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import({ShipQueryRepository.class, QuerydslConfig.class})
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipRepositoryTest extends BaseTest {

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private ShipJpaRepository shipJpaRepository;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<Ship> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Ship.class)
		.set("shipName", englishStringLength)
		.set("shipNumber", englishStringLength)
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

	@Test
	@DisplayName("회원 ID로 등록된 선박 개수 조회 [Repository] - Success")
	void t03() {
		// Given
		Long givenMemberId = 1L;

		shipJpaRepository.deleteAll();

		List<Ship> givenShip1 = arbitraryBuilder
			.set("shipId", null)
			.set("memberId", givenMemberId)
			.sampleList(5);

		List<Ship> givenShip2 = arbitraryBuilder
			.set("shipId", null)
			.set("memberId", 2L)
			.sampleList(7);

		List<Ship> savedShip1 = shipJpaRepository.saveAll(givenShip1);
		shipJpaRepository.saveAll(givenShip2);

		// When
		Long countByMemberId = shipRepository.countByMemberId(givenMemberId);

		// Then
		assertThat(countByMemberId).isEqualTo(savedShip1.size());
	}

	@Test
	@DisplayName("회원 ID로 등록된 선박 조회 [Repository] - Success")
	void t04() {
		// Given
		Long givenMemberId = 1L;

		shipJpaRepository.deleteAll();

		List<Ship> givenShip1 = arbitraryBuilder
			.set("shipId", null)
			.set("memberId", givenMemberId)
			.sampleList(5);

		List<Ship> givenShip2 = arbitraryBuilder
			.set("shipId", null)
			.set("memberId", 2L)
			.sampleList(7);

		List<Ship> savedShip1 = shipJpaRepository.saveAll(givenShip1);
		shipJpaRepository.saveAll(givenShip2);

		// When
		List<ShipResponse.Detail> findShipAllList = shipRepository.findDetailAll(givenMemberId);

		// Then
		assertThat(findShipAllList).hasSize(savedShip1.size());
	}

	@Test
	@DisplayName("선박 삭제 [Repository] - Success")
	void t05() {
		// Given
		Long givenMemberId = 2L;

		shipJpaRepository.deleteAll();

		Ship givenShip = arbitraryBuilder
			.set("shipId", null)
			.set("memberId", givenMemberId)
			.sample();

		Ship savedShip = shipJpaRepository.save(givenShip);

		// When
		shipRepository.deleteByShipId(savedShip.getShipId());
		Optional<Ship> findShip = shipRepository.findById(savedShip.getShipId());

		// Then
		assertThat(findShip).isEmpty();
	}
}

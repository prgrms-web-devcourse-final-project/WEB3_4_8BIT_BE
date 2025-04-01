package com.backend.domain.shipfishingpostfish.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostFishRepositoryTest extends BaseTest {

	@Autowired
	private ShipFishingPostFishRepository shipFishingPostFishRepository;

	@Test
	@DisplayName("선상 낚시 게시글, 어류 중간 테이블 entity save [Repository] - Success")
	void t01() {
		// Given
		ShipFishingPostFish givenShipFishingPostFish = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPostFish.class)
			.sample();

		// When
		ShipFishingPostFish savedShipFishingPostFish = shipFishingPostFishRepository.save(givenShipFishingPostFish);

		// Then
		assertThat(savedShipFishingPostFish).isNotNull();
		assertThat(savedShipFishingPostFish.getShipFishingPostId()).isNotNull();
	}

	@Test
	@DisplayName("선상 낚시 게시글, 어류 중간 테이블 entity saveAll & findAll [Repository] - Success")
	void t02() {
		// Given
		List<ShipFishingPostFish> givenShipFishingPostFishList = fixtureMonkeyBuilder.
			giveMeBuilder(ShipFishingPostFish.class)
			.sampleList(5);

		// When
		shipFishingPostFishRepository.saveAll(givenShipFishingPostFishList);

		// Then
		List<ShipFishingPostFish> savedShipFishingPostFish = shipFishingPostFishRepository.findAll();

		assertThat(savedShipFishingPostFish.size()).isEqualTo(givenShipFishingPostFishList.size());
	}

}

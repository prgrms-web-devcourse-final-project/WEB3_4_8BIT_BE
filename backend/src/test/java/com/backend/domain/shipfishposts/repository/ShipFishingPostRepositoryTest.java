package com.backend.domain.shipfishposts.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.QuerydslConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostRepositoryTest extends BaseTest {

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Repository] - Success")
	void t01() {
		// Given
		ShipFishingPost givenShipFishingPost = fixtureMonkeyBuilder
			.giveMeBuilder(ShipFishingPost.class)
			.set("shipFishingPostId", null)
			.set("subject", "1555")
			.sample();

		// When
		ShipFishingPost saved = shipFishingPostRepository.save(givenShipFishingPost);

		// Then
		assertThat(saved).isNotNull();
		assertThat(saved.getShipFishingPostId()).isNotNull();
	}
}

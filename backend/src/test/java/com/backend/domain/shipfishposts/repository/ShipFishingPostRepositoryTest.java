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

import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.QuerydslConfig;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostRepositoryTest extends BaseTest {

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	private final ArbitraryBuilder<ShipFishingPost> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(ShipFishingPost.class)
		.set("subject", "1555");

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Repository] - Success")
	void t01() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder
			.set("shipFishingPostId", null)
			.sample();

		// When
		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// Then
		assertThat(savedShipFishingPost).isNotNull();
		assertThat(savedShipFishingPost.getShipFishingPostId()).isNotNull();
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [Repository] - Success")
	void t02() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder
			.set("shipFishingPostId", null)
			.sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// When
		ShipFishingPostResponse.Detail findDetail = shipFishingPostRepository
			.findDetailById(savedShipFishingPost.getShipFishingPostId()).get();

		// Then
		assertThat(findDetail.shipFishingPostId()).isEqualTo(savedShipFishingPost.getShipFishingPostId());
		assertThat(findDetail.subject()).isEqualTo(savedShipFishingPost.getSubject());
		assertThat(findDetail.content()).isEqualTo(savedShipFishingPost.getContent());
		assertThat(findDetail.imageList().toString()).isEqualTo(savedShipFishingPost.getImageList().toString());
		assertThat(findDetail.startTime()).isEqualTo(savedShipFishingPost.getStartTime());
		assertThat(findDetail.durationTime()).isEqualTo(savedShipFishingPost.getDurationTime());
		assertThat(findDetail.maxGuestCount()).isEqualTo(savedShipFishingPost.getMaxGuestCount());
		assertThat(findDetail.reviewEverRate()).isEqualTo(savedShipFishingPost.getReviewEverRate());
	}
}

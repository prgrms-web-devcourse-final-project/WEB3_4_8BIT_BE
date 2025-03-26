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

import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import com.backend.domain.shipfishposts.util.ShipFishPostsUtil;
import com.backend.global.config.QuerydslConfig;

@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishPostsRepositoryTest {

	@Autowired
	private ShipFishPostsRepositoryImpl shipFishPostsRepositoryImpl;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Repository] - Success")
	void t01() {
		// Given
		ShipFishPosts givenShipFishPosts = ShipFishPostsUtil.createShipFishPostsWithNullableId();

		// When
		ShipFishPosts saved = shipFishPostsRepositoryImpl.save(givenShipFishPosts);

		// Then
		assertThat(saved).isNotNull();
		assertThat(saved.getShipFishPostId()).isNotNull();
	}

}

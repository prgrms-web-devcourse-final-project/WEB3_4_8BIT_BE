package com.backend.domain.shipfishposts.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.domain.shipfishposts.Util.BaseTest;
import com.backend.domain.shipfishposts.dto.converter.ShipFishPostsConverter;
import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import com.backend.domain.shipfishposts.repository.ShipFishPostsRepository;

@ExtendWith(MockitoExtension.class)
public class ShipFishPostsServiceTest extends BaseTest {

	@InjectMocks
	private ShipFishPostsServiceImpl shipFishPostsService;

	@Mock
	private ShipFishPostsRepository shipFishPostsRepository;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		//Given
		ShipFishPostsRequest.Create givenRequestDto =
			fixtureMonkeyValidation.giveMeOne(ShipFishPostsRequest.Create.class);

		Long durationMinute = Duration.between(givenRequestDto.startDate(), givenRequestDto.endDate()).toMinutes();

		ShipFishPosts givenShipFishPosts = ShipFishPostsConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			durationMinute);

		ReflectionTestUtils.setField(givenShipFishPosts, "shipFishPostId", 1L);

		// When
		when(shipFishPostsRepository.save(givenShipFishPosts)).thenReturn(givenShipFishPosts);

		Long savedId = shipFishPostsService.save(givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}
}

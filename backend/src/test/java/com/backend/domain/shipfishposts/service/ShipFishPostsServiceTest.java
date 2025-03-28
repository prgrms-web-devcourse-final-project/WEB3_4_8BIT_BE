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

import com.backend.domain.shipfishposts.converter.ShipFishPostsConverter;
import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import com.backend.domain.shipfishposts.repository.ShipFishPostsRepository;
import com.backend.global.Util.BaseTest;

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

		Duration durationTime = Duration.between(givenRequestDto.startTime(), givenRequestDto.endTime());

		if (durationTime.isNegative()) {
			durationTime = durationTime.plusDays(1);
		}

		long totalMinutes = durationTime.toMinutes();

		String durationMinute = String.format("%02d:%02d", totalMinutes / 60, totalMinutes % 60);

		ShipFishPosts givenShipFishPosts = ShipFishPostsConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			durationMinute);

		ReflectionTestUtils.setField(givenShipFishPosts, "shipFishPostId", 1L);

		// When
		when(shipFishPostsRepository.save(any(ShipFishPosts.class))).thenReturn(givenShipFishPosts);

		Long savedId = shipFishPostsService.save(givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}
}

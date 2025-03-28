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

import com.backend.domain.shipfishingpost.converter.ShipFishingPostConverter;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpost.service.ShipFishingPostServiceImpl;
import com.backend.global.Util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ShipFishingPostServiceTest extends BaseTest {

	@InjectMocks
	private ShipFishingPostServiceImpl shipFishingPostService;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		//Given
		ShipFishingPostRequest.Create givenRequestDto =
			fixtureMonkeyValidation.giveMeOne(ShipFishingPostRequest.Create.class);

		Duration durationTime = Duration.between(givenRequestDto.startTime(), givenRequestDto.endTime());

		if (durationTime.isNegative()) {
			durationTime = durationTime.plusDays(1);
		}

		long totalMinutes = durationTime.toMinutes();

		String durationMinute = String.format("%02d:%02d", totalMinutes / 60, totalMinutes % 60);

		ShipFishingPost givenShipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(givenRequestDto,
			durationMinute);

		ReflectionTestUtils.setField(givenShipFishingPost, "shipFishingPostId", 1L);

		// When
		when(shipFishingPostRepository.save(any(ShipFishingPost.class))).thenReturn(givenShipFishingPost);

		Long savedId = shipFishingPostService.save(givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}
}

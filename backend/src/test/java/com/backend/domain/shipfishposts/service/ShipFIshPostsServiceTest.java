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

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import com.backend.domain.shipfishposts.repository.ShipFishPostsRepositoryImpl;
import com.backend.domain.shipfishposts.util.ShipFishPostsUtil;

@ExtendWith(MockitoExtension.class)
public class ShipFIshPostsServiceTest {

	@InjectMocks
	private ShipFishPostsServiceImpl shipFishPostsServiceImpl;

	@Mock
	private ShipFishPostsRepositoryImpl shipFishPostsRepositoryImpl;

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Service] - Success")
	void t01() {
		//Given
		ShipFishPostsRequest.Create givenRequestDto = ShipFishPostsUtil.createShipFishPostsRequestCreate();

		Long durationMinute = Duration.between(givenRequestDto.startDate(), givenRequestDto.endDate()).toMinutes();

		ShipFishPosts givenShipFishPosts = ShipFishPosts.from(givenRequestDto, durationMinute);

		ReflectionTestUtils.setField(givenShipFishPosts, "shipFishPostId", 1L);

		// When
		when(shipFishPostsRepositoryImpl.save(any(ShipFishPosts.class))).thenReturn(givenShipFishPosts);

		Long savedId = shipFishPostsServiceImpl.save(givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(1L);
	}

}

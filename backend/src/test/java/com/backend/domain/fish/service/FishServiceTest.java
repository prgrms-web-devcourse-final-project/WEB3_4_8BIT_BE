package com.backend.domain.fish.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class FishServiceTest extends BaseTest {

	@Mock
	private FishRepository fishRepository;

	@InjectMocks
	private FishServiceImpl fishServiceImpl;

	@Test
	@DisplayName("물고기 상세 조회 [Service] - Success")
	void t01() {
		// Given
		Long fishId = 1L;

		FishResponse.Detail givenDetail = fixtureMonkeyRecord.giveMeOne(FishResponse.Detail.class);

		when(fishRepository.findDetailById(fishId)).thenReturn(Optional.ofNullable(givenDetail));

		// When
		FishResponse.Detail getDetail = fishServiceImpl.getFishDetail(fishId);

		// Then
		assertThat(getDetail).isEqualTo(givenDetail);
	}

	@Test
	@DisplayName("물고기 상세 조회 [NotFound] [Service] - Fail")
	void t02() {
		// Given
		Long givenFishId = 1L;

		// When & Then
		assertThatThrownBy(
			() -> fishServiceImpl.getFishDetail(givenFishId))
			.isExactlyInstanceOf(FishException.class)
			.hasMessage(FishErrorCode.FISH_NOT_FOUND.getMessage());
	}
}
package com.backend.domain.fishpoint.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse;
import com.backend.domain.fishpointsummary.service.FishPointSummaryService;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class FishPointServiceTest extends BaseTest {

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private FishPointSummaryService fishPointSummaryService;

	@InjectMocks
	private FishPointServiceImpl fishPointServiceImpl;

	@Test
	@DisplayName("지역 ID로 낚시 포인트 목록 조회 [Service] - Success")
	void t01() {
		// given
		Long regionId = 13L;
		List<FishPointResponse.Basic> mockResponse = fixtureMonkeyRecord.giveMe(FishPointResponse.Basic.class, 2);
		given(fishPointRepository.findByRegionId(regionId)).willReturn(mockResponse);

		// when
		List<FishPointResponse.Basic> result = fishPointServiceImpl.getFishPointsByRegionId(regionId);

		// then
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("포인트 이름으로 낚시 포인트 검색 [Service] - Success")
	void t02() {
		// given
		String name = "고흥";
		List<FishPointResponse.Basic> mockResponse = fixtureMonkeyRecord.giveMe(FishPointResponse.Basic.class, 3);
		given(fishPointRepository.findByFishPointName(name)).willReturn(mockResponse);

		// when
		List<FishPointResponse.Basic> result = fishPointServiceImpl.searchFishPoints(name);

		// then
		assertThat(result).hasSize(3);
	}

	@Test
	@DisplayName("인기 낚시 포인트 Top3 조회 [Service] - Success")
	void t03() {
		// given
		List<FishPointResponse.Popularity> mockResult = fixtureMonkeyRecord.giveMe(FishPointResponse.Popularity.class, 3);
		given(fishPointRepository.findPopularityFishPoints()).willReturn(mockResult);

		// when
		List<FishPointResponse.Popularity> result = fishPointServiceImpl.getPopularityFishPoints();

		// then
		assertThat(result).hasSize(3);
	}

	@Test
	@DisplayName("낚시 포인트 ID로 상세 정보 조회 [Service] - Success")
	void t04() {
		// given
		FishPoint fishPoint = createRandomFishPoint(13L, "울산 북구");
		Long fishPointId = 1L;
		List<FishPointSummaryResponse.Basic> summaryList = fixtureMonkeyRecord.giveMe(FishPointSummaryResponse.Basic.class, 2);

		given(fishPointRepository.findByFishPointId(fishPointId)).willReturn(Optional.of(fishPoint));
		given(fishPointSummaryService.getFishPointSummaries(fishPointId)).willReturn(summaryList);

		// when
		FishPointResponse.Detail detail = fishPointServiceImpl.getFishPointDetail(fishPointId);

		// then
		assertThat(detail.fishPointName()).isEqualTo(fishPoint.getFishPointName());
		assertThat(detail.fishList()).hasSize(2);
	}
}
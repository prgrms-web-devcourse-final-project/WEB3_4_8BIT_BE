package com.backend.domain.fishpointsummary.service;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpointsummary.entity.FishPointSummary;
import com.backend.domain.fishpointsummary.repository.FishPointSummaryRepository;
import com.backend.global.util.BaseTest;
import com.querydsl.core.Tuple;

@ExtendWith(MockitoExtension.class)
class FishPointSummaryServiceTest extends BaseTest {

	@Mock
	private FishPointSummaryRepository fishPointSummaryRepository;

	@Mock
	private FishEncyclopediaRepository fishEncyclopediaRepository;

	@Mock
	private FishRepository fishRepository;

	@InjectMocks
	private FishPointSummaryServiceImpl fishPointSummaryService;

	@Test
	@DisplayName("낚시 포인트 ID로 물고기 집계 정보 조회 [Service] - Success")
	void getFishPointSummariesTest() {
		// given
		Long givenFishPointId = 1L;
		List<Basic> givenSumaryBasicList = fixtureMonkeyValidation.giveMe(Basic.class, 4);

		given(fishPointSummaryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(givenFishPointId))
			.willReturn(givenSumaryBasicList);

		// when
		List<Basic> result = fishPointSummaryService.getFishPointSummaries(givenFishPointId);

		// then
		assertThat(result).hasSize(4);
		verify(fishPointSummaryRepository).findTop4ByFishPointIdOrderByTotalCountDesc(givenFishPointId);
	}

	@Test
	@DisplayName("최근 1시간 어류 도감 업데이트 없으면 집계 X [Service] - Success")
	void noRecentFishingRecordTest() {
		// given
		given(fishEncyclopediaRepository.findFishPointHourlyFishCountSummary()).willReturn(List.of());

		// when
		fishPointSummaryService.updateFishPointSummariesHourly();

		// then
		verify(fishPointSummaryRepository, never()).saveAll(any());
	}

	@Test
	@DisplayName("최근 기록 기반 요약 정보 생성 [Service] - 기존 Summary 없음 - Success")
	void updateFishPointSummariesHourly_createNew() {
		// given
		Long fishPointId = 1L;
		Long fishId = 10L;
		Integer count = 5;

		Tuple tuple = mock(Tuple.class);
		given(tuple.get(0, Long.class)).willReturn(fishPointId);
		given(tuple.get(1, Long.class)).willReturn(fishId);
		given(tuple.get(2, Integer.class)).willReturn(count);

		given(fishEncyclopediaRepository.findFishPointHourlyFishCountSummary())
			.willReturn(List.of(tuple));

		given(fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(Set.of(fishPointId), Set.of(fishId)))
			.willReturn(List.of());

		Fish fish = fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
			.set("fishId", fishId)
			.set("fileId", 999L)
			.sample();

		given(fishRepository.findAllById(List.of(fishId)))
			.willReturn(List.of(fish));

		// when
		fishPointSummaryService.updateFishPointSummariesHourly();

		// then
		verify(fishPointSummaryRepository).saveAll(argThat(list -> {
			assertThat(list).hasSize(1);
			FishPointSummary s = list.get(0);
			assertThat(s.getFishPointId()).isEqualTo(fishPointId);
			assertThat(s.getFishId()).isEqualTo(fishId);
			assertThat(s.getTotalCount()).isEqualTo(count);
			assertThat(s.getFileId()).isEqualTo(999L);
			return true;
		}));
	}

	@Test
	@DisplayName("최근 기록 기반 요약 정보 누적 [Service] - 기존 Summary 존재 - Success")
	void updateFishPointSummariesHourly_increaseExisting() {
		// given
		Long fishPointId = 1L;
		Long fishId = 1L;
		Integer addedCount = 5;

		Tuple tuple = mock(Tuple.class);
		given(tuple.get(0, Long.class)).willReturn(fishPointId);
		given(tuple.get(1, Long.class)).willReturn(fishId);
		given(tuple.get(2, Integer.class)).willReturn(addedCount);

		given(fishEncyclopediaRepository.findFishPointHourlyFishCountSummary())
			.willReturn(List.of(tuple));

		// 기존 summary: totalCount = 10
		FishPointSummary existingSummary = fixtureMonkeyBuilder.giveMeBuilder(FishPointSummary.class)
			.set("fishPointId", fishPointId)
			.set("fishId", fishId)
			.set("totalCount", 10)
			.set("fileId", 999L)
			.sample();

		given(fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(Set.of(fishPointId), Set.of(fishId)))
			.willReturn(List.of(existingSummary));

		given(fishRepository.findAllById(List.of(fishId)))
			.willReturn(List.of());

		// when
		fishPointSummaryService.updateFishPointSummariesHourly();

		// then
		verify(fishPointSummaryRepository).saveAll(argThat(list ->
			list.size() == 1 &&
				list.get(0).getFishPointId().equals(fishPointId) &&
				list.get(0).getFishId().equals(fishId) &&
				list.get(0).getTotalCount().equals(15) &&
				list.get(0).getFileId().equals(999L)
		));
	}

	@Test
	@DisplayName("낚시 포인트 집계 실패 [Service] - Fail")
	void updateFishPointSummariesHourly_fishNotFound() {
		// given
		Long fishPointId = 1L;
		Long fishId = 10L;

		Tuple tuple = mock(Tuple.class);
		given(tuple.get(0, Long.class)).willReturn(fishPointId);
		given(tuple.get(1, Long.class)).willReturn(fishId);
		given(tuple.get(2, Integer.class)).willReturn(7);

		given(fishEncyclopediaRepository.findFishPointHourlyFishCountSummary())
			.willReturn(List.of(tuple));

		given(fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(Set.of(fishPointId), Set.of(fishId)))
			.willReturn(List.of());

		given(fishRepository.findAllById(List.of(fishId)))
			.willReturn(List.of());

		// when & then
		assertThatThrownBy(() -> fishPointSummaryService.updateFishPointSummariesHourly())
			.isInstanceOf(FishException.class)
			.hasMessage("물고기가 존재하지 않습니다.");
	}
}
package com.backend.domain.fishpointsummary.service;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpointsummary.converter.FishPointSummaryConverter;
import com.backend.domain.fishpointsummary.entity.FishPointSummary;
import com.backend.domain.fishpointsummary.repository.FishPointSummaryRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishPointSummaryServiceImpl implements FishPointSummaryService {

	private final FishPointSummaryRepository fishPointSummaryRepository;
	private final FishEncyclopediaRepository fishEncyclopediaRepository;
	private final FishRepository fishRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Basic> getFishPointSummaries(final Long fishPointId) {
		return fishPointSummaryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(fishPointId);
	}

	@Override
	@Transactional
	public void updateFishPointSummariesHourly() {
		// 1. 최근 1시간 동안의 포인트별 어종별 낚시 수 데이터 조회
		List<Tuple> tupleList = fishEncyclopediaRepository.findFishPointHourlyFishCountSummary();

		if (tupleList.isEmpty()){
			log.debug("[FishPointSummaryService] 최근 1시간 내 낚시 기록 없음");
			return;
		}

		// 2. 조회 결과에서 fishPointId, fishId만 추출해 Set 으로 중복 제거
		Set<Long> fishPointIdSet = new HashSet<>();
		Set<Long> fishIdSet = new HashSet<>();
		for (Tuple tuple : tupleList) {
			fishPointIdSet.add(tuple.get(0, Long.class));
			fishIdSet.add(tuple.get(1, Long.class));
		}

		// 3. 기존 fishPointSummary 데이터를 한 번에 조회한 후 Map으로 구성
		List<FishPointSummary> existingSummaries = fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(
			fishPointIdSet,
			fishIdSet
		);
		Map<Pair<Long, Long>, FishPointSummary> summaryMap = existingSummaries.stream()
			.collect(Collectors.toMap(
				s -> Pair.of(s.getFishPointId(), s.getFishId()),
				Function.identity()
			));

		// 4. fish 엔티티 미리 조회해서 Map 으로 구성
		Map<Long, Fish> fishMap = fishRepository.findAllById(new ArrayList<>(fishIdSet))
			.stream()
			.collect(Collectors.toMap(Fish::getFishId, Function.identity()));

		// 5. 저장할 FishPointSummary 리스트 생성
		List<FishPointSummary> fishPointSummaryList = new ArrayList<>();

		// 6. 튜플을 순회하며 기존 fishPointSummary는 count 누적, 없으면 새로 생성
		for (Tuple tuple : tupleList) {
			Long fishPointId = Objects.requireNonNull(tuple.get(0, Long.class));
			Long fishId = Objects.requireNonNull(tuple.get(1, Long.class));
			Integer addedCount = Optional.ofNullable(tuple.get(2, Integer.class)).orElse(0);

			Pair<Long, Long> key = Pair.of(fishPointId, fishId);
			FishPointSummary fishPointSummary = summaryMap.get(key);

			if (fishPointSummary != null) {
				fishPointSummary.increaseTotalCount(addedCount);
			} else {
				Fish fish = fishMap.get(fishId);
				if (fish == null) {
					throw new FishException(FishErrorCode.FISH_NOT_FOUND);
				}

				fishPointSummary = FishPointSummaryConverter.fromCreate(
					fishPointId,
					fishId,
					fish.getFileId(),
					addedCount
				);
			}

			fishPointSummaryList.add(fishPointSummary);
		}

		// 7. 변경/생성된 fishPointSummary 저장
		fishPointSummaryRepository.saveAll(fishPointSummaryList);
		log.debug("[FishPointSummaryService] 집계 완료 - 저장된 summary 수: {}", fishPointSummaryList.size());
	}
}

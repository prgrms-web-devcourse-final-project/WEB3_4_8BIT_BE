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
		List<Tuple> tupleList = getHourlyFishCountSummary();

		if (tupleList.isEmpty()) {
			log.debug("[FishPointSummaryService] 최근 1시간 내 낚시 기록 없음");
			return;
		}

		Pair<Set<Long>, Set<Long>> pair = extractFishPointAndFishIds(tupleList);
		Map<Pair<Long, Long>, FishPointSummary> summaryMap = getExistingSummaryMap(pair);
		Map<Long, Fish> fishMap = getFishMap(pair.getSecond());

		List<FishPointSummary> updatedSummaries = buildUpdatedSummaries(tupleList, summaryMap, fishMap);
		saveSummaries(updatedSummaries);

		log.debug("[FishPointSummaryService] 집계 완료 - 저장된 summary 수: {}", updatedSummaries.size());
	}

	/**
	 * 최근 1시간 동안 포인트별 어종별 낚시 기록 요약 데이터를 조회
	 *
	 * @return Tuple 리스트 (fishPointId, fishId, count)
	 */
	private List<Tuple> getHourlyFishCountSummary() {
		return fishEncyclopediaRepository.findFishPointHourlyFishCountSummary();
	}

	/**
	 * Tuple 리스트에서 fishPointId와 fishId를 추출하여 Set으로 반환
	 *
	 * @param tupleList 낚시 기록 요약 데이터
	 * @return Pair(포인트 ID Set, 어종 ID Set)
	 */
	private Pair<Set<Long>, Set<Long>> extractFishPointAndFishIds(final List<Tuple> tupleList) {
		Set<Long> fishPointIdSet = new HashSet<>();
		Set<Long> fishIdSet = new HashSet<>();

		for (Tuple tuple : tupleList) {
			fishPointIdSet.add(tuple.get(0, Long.class));
			fishIdSet.add(tuple.get(1, Long.class));
		}
		return Pair.of(fishPointIdSet, fishIdSet);
	}

	/**
	 * 기존에 저장된 FishPointSummary 엔티티를 조회하여 (fishPointId, fishId) 기준으로 Map을 생성
	 *
	 * @param pair Pair(포인트 ID Set, 어종 ID Set)
	 * @return (fishPointId, fishId) → FishPointSummary Map
	 */
	private Map<Pair<Long, Long>, FishPointSummary> getExistingSummaryMap(final Pair<Set<Long>, Set<Long>> pair) {
		List<FishPointSummary> existingSummaries = fishPointSummaryRepository.findByFishPointIdInAndFishIdIn(
			pair.getFirst(),
			pair.getSecond()
		);

		return existingSummaries.stream()
			.collect(Collectors.toMap(
				s -> Pair.of(s.getFishPointId(), s.getFishId()),
				Function.identity()
			));
	}

	/**
	 * 어종 ID Set을 기준으로 Fish 엔티티를 한 번에 조회하여 Map으로 반환
	 *
	 * @param fishIdSet 어종 ID Set
	 * @return fishId → Fish Map
	 */
	private Map<Long, Fish> getFishMap(final Set<Long> fishIdSet) {
		return fishRepository.findAllById(new ArrayList<>(fishIdSet))
			.stream()
			.collect(Collectors.toMap(Fish::getFishId, Function.identity()));
	}

	/**
	 * Tuple 리스트를 순회하며 기존 FishPointSummary에 count를 누적하거나,
	 * 없으면 새로 생성하여 리스트로 반환
	 *
	 * @param tupleList 요약 데이터 Tuple 리스트
	 * @param summaryMap 기존 요약 데이터 Map
	 * @param fishMap 어종 ID 기준 Fish Map
	 * @return 생성되거나 업데이트된 FishPointSummary 리스트
	 */
	private List<FishPointSummary> buildUpdatedSummaries(
		final List<Tuple> tupleList,
		final Map<Pair<Long, Long>, FishPointSummary> summaryMap,
		final Map<Long, Fish> fishMap
	) {

		List<FishPointSummary> result = new ArrayList<>();

		for (Tuple tuple : tupleList) {
			Long fishPointId = Objects.requireNonNull(tuple.get(0, Long.class));
			Long fishId = Objects.requireNonNull(tuple.get(1, Long.class));
			Integer addedCount = Optional.ofNullable(tuple.get(2, Integer.class)).orElse(0);

			Pair<Long, Long> key = Pair.of(fishPointId, fishId);
			FishPointSummary summary = summaryMap.get(key);

			if (summary != null) {
				summary.increaseTotalCount(addedCount);
			} else {
				Fish fish = fishMap.get(fishId);
				if (fish == null) {
					throw new FishException(FishErrorCode.FISH_NOT_FOUND);
				}
				summary = FishPointSummaryConverter.fromCreate(
					fishPointId,
					fishId,
					fish.getFileId(),
					addedCount
				);
			}
			result.add(summary);
		}
		return result;
	}

	/**
	 * 생성되거나 수정된 FishPointSummary 리스트를 저장소에 일괄 저장
	 *
	 * @param summaries 저장할 FishPointSummary 리스트
	 */
	private void saveSummaries(final List<FishPointSummary> summaries) {
		fishPointSummaryRepository.saveAll(summaries);
	}
}

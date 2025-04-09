package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishPointSummaryRepositoryImpl implements FishPointSummaryRepository {

	private final FishPointSummaryJpaRepository fishPointSummaryJpaRepository;
	private final FishPointSummaryQueryRepository fishPointSummaryQueryRepository;

	@Override
	public List<Basic> findTop4ByFishPointIdOrderByTotalCountDesc(final Long fishPointId) {
		return fishPointSummaryQueryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(fishPointId);
	}

	@Override
	public List<FishPointSummary> findByFishPointIdInAndFishIdIn(
		final Set<Long> fishPointIdList,
		final Set<Long> fishIdList
	) {
		return fishPointSummaryJpaRepository.findByFishPointIdInAndFishIdIn(fishPointIdList, fishIdList);
	}

	@Override
	public void saveAll(final List<FishPointSummary> fishPointSummaryList) {
		fishPointSummaryJpaRepository.saveAll(fishPointSummaryList);
	}
}

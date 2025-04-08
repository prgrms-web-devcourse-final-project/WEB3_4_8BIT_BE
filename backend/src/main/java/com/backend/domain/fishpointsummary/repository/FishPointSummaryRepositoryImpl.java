package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishPointSummaryRepositoryImpl implements FishPointSummaryRepository {

	private final FishPointSummaryJpaRepository fishPointSummaryJpaRepository;
	private final FishPointSummaryQueryRepository fishPointSummaryQueryRepository;

	@Override
	public List<Basic> findTop4ByFishPointIdOrderByTotalCountDesc(Long fishPointId) {
		return fishPointSummaryQueryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(fishPointId);
	}
}

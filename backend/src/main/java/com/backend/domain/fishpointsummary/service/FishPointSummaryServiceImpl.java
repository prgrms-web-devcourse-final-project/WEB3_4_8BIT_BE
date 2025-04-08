package com.backend.domain.fishpointsummary.service;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fishpointsummary.repository.FishPointSummaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishPointSummaryServiceImpl implements FishPointSummaryService {

	private final FishPointSummaryRepository fishPointSummaryRepository;

	@Override
	public List<Basic> getFishPointSummaries(final Long fishPointId) {
		return fishPointSummaryRepository.findTop4ByFishPointIdOrderByTotalCountDesc(fishPointId);
	}
}

package com.backend.domain.fishpoint.service;

import static com.backend.domain.fishpoint.converter.FishPointConverter.*;
import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse;
import com.backend.domain.fishpointsummary.service.FishPointSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishPointServiceImpl implements FishPointService {

	private final FishPointRepository fishPointRepository;
	private final FishPointSummaryService fishPointSummaryService;

	@Override
	@Transactional(readOnly = true)
	public List<Basic> getFishPointsByBounds(
		final double swLat,
		final double swLng,
		final double neLat,
		final double neLng
	) {
		return fishPointRepository.findByBounds(swLat, swLng, neLat, neLng);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WithDistance> getNearbyFishPoints(final double lat, final double lng, final double radiusKm) {
		return fishPointRepository.findByDistanceWithin(lat, lng, radiusKm);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WithDistance> getNearestFishPoints(final double lat, final double lng) {
		return fishPointRepository.findNearestFishPoints(lat, lng);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Basic> getFishPointsByRegionId(final Long regionId) {
		return fishPointRepository.findByRegionId(regionId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Basic> searchFishPoints(final String fishPointName) {
		return fishPointRepository.findByFishPointName(fishPointName);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Popularity> getPopularityFishPoints() {
		return fishPointRepository.findPopularityFishPoints();
	}

	@Override
	@Transactional(readOnly = true)
	public Detail getFishPointDetail(final Long fishPointId) {
		FishPoint fishPoint = getFishPoint(fishPointId);

		List<FishPointSummaryResponse.Basic> fishPointSummaryList =
			fishPointSummaryService.getFishPointSummaries(fishPointId);

		return fromEntityAndSummary(fishPoint, fishPointSummaryList);
	}

	private FishPoint getFishPoint(final Long fishPointId) {
		return fishPointRepository.findByFishPointId(fishPointId).orElseThrow(
			() -> new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND));
	}
}

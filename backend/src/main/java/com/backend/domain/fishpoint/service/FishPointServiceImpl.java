package com.backend.domain.fishpoint.service;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fishpoint.repository.FishPointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishPointServiceImpl implements FishPointService {

	private final FishPointRepository fishPointRepository;

	@Override
	public List<Response> getFishPointsByBounds(
		final double swLat,
		final double swLng,
		final double neLat,
		final double neLng
	) {
		return fishPointRepository.findByBounds(swLat, swLng, neLat, neLng);
	}

	@Override
	public List<ResponseWithDistance> getNearbyFishPoints(final double lat, final double lng, final double radiusKm) {
		return fishPointRepository.findByDistanceWithin(lat, lng, radiusKm);
	}

	@Override
	public List<ResponseWithDistance> getNearestFishPoints(final double lat, final double lng) {
		return fishPointRepository.findNearestFishPoints(lat, lng);
	}

	@Override
	public List<Response> searchFishPoints(final String fishPointName) {
		return fishPointRepository.findByFishPointName(fishPointName);
	}
}

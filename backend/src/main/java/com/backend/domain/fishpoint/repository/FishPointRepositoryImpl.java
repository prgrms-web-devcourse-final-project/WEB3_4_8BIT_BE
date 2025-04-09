package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishpoint.entity.FishPoint;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishPointRepositoryImpl implements FishPointRepository {

	private final FishPointJpaRepository fishPointJpaRepository;
	private final FishPointQueryRepository fishPointQueryRepository;

	@Override
	public boolean existsById(final Long fishPointId) {
		return fishPointJpaRepository.existsById(fishPointId);
	}

	@Override
	public FishPoint save(final FishPoint fishPoint) {
		return fishPointJpaRepository.save(fishPoint);
	}

	@Override
	public List<Basic> findByBounds(final double swLat, final double swLng, final double neLat, final double neLng) {
		return fishPointQueryRepository.findByBounds(swLat, swLng, neLat, neLng);
	}

	@Override
	public List<WithDistance> findByDistanceWithin(final double lat, final  double lng, final double radiusKm) {
		return fishPointQueryRepository.findByDistanceWithin(lat, lng, radiusKm);
	}

	@Override
	public List<WithDistance> findNearestFishPoints(final double lat, final double lng) {
		return fishPointQueryRepository.findByNearestFishPoints(lat, lng);
	}

	@Override
	public List<Basic> findByRegionId(final Long regionId) {
		return fishPointQueryRepository.findByRegionId(regionId);
	}

	@Override
	public List<Basic> findByFishPointName(final String fishPointName) {
		return fishPointQueryRepository.findByFishPointName(fishPointName);
	}

	@Override
	public List<Popularity> findPopularityFishPoints() {
		return fishPointQueryRepository.findPopularityFishPoints();
	}

	@Override
	public Optional<FishPoint> findByFishPointId(final Long fishPointId) {
		return fishPointJpaRepository.findById(fishPointId);
	}
}

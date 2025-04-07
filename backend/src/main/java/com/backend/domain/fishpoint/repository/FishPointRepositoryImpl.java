package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

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
	public List<Response> findByBounds(double swLat, double swLng, double neLat, double neLng) {
		return fishPointQueryRepository.findByBounds(swLat, swLng, neLat, neLng);
	}

	@Override
	public List<ResponseWithDistance> findByDistanceWithin(final double lat, final  double lng, final double radiusKm) {
		return fishPointQueryRepository.findByDistanceWithin(lat, lng, radiusKm);
	}

	@Override
	public List<Response> findByFishPointName(final String fishPointName) {
		return fishPointQueryRepository.findByFishPointName(fishPointName);
	}
}

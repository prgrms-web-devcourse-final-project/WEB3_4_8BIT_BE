package com.backend.domain.fishpoint.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishpoint.entity.FishPoint;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishPointRepositoryImpl implements FishPointRepository {

	private final FishPointJpaRepository fishPointJpaRepository;

	@Override
	public boolean existsById(final Long fishPointId) {
		return fishPointJpaRepository.existsById(fishPointId);
	}

	@Override
	public FishPoint save(final FishPoint fishPoint) {
		return fishPointJpaRepository.save(fishPoint);
	}
}

package com.backend.domain.fish.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.fish.entity.Fish;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishRepositoryImpl implements FishRepository {
	private final FishJpaRepository fishJpaRepository;

	@Override
	public boolean existsById(final Long fishId) {
		return fishJpaRepository.existsById(fishId);
	}

	@Override
	public Fish save(final Fish fish) {
		return fishJpaRepository.save(fish);
	}

	@Override
	public List<Fish> findAllById(final List<Long> fishIdList) {
		return fishJpaRepository.findAllById(fishIdList);
	}
}

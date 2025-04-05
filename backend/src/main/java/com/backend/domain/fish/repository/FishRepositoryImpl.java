package com.backend.domain.fish.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.entity.Fish;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishRepositoryImpl implements FishRepository {

	private final FishJpaRepository fishJpaRepository;
	private final FishQueryRepository fishQueryRepository;

	@Override
	public boolean existsById(final Long fishId) {
		return fishJpaRepository.existsById(fishId);
	}

	@Override
	public Fish save(final Fish fish) {
		return fishJpaRepository.save(fish);
	}

	@Override
	public Optional<FishResponse.Detail> findDetailById(final Long fishId) {
		return fishQueryRepository.findDetailById(fishId);
	}

	@Override
	public List<Fish> findAllById(final List<Long> fishIdList) {
		return fishJpaRepository.findAllById(fishIdList);
	}

	@Override
	public List<FishResponse.Popular> findPopular(final Integer size) {
		return fishQueryRepository.findPopular(size);
	}
}

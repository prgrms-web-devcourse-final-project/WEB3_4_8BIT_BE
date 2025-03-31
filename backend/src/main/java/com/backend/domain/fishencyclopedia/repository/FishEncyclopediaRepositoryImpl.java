package com.backend.domain.fishencyclopedia.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaRepositoryImpl implements FishEncyclopediaRepository {
	private final FishEncyclopediaJpaRepository fishEncyclopediaJpaRepository;

	@Override
	public FishEncyclopedia createFishEncyclopedia(final FishEncyclopedia fishEncyclopedia) {
		return fishEncyclopediaJpaRepository.save(fishEncyclopedia);
	}
}

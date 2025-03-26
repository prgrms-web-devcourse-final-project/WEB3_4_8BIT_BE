package com.backend.domain.fishencyclopedia.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaRepositoryImpl implements FishEncyclopediaRepository {
	private final FishEncyclopediaJpaRepository fishEncyclopediaJpaRepository;

	@Override
	public FishEncyclopedias save(FishEncyclopedias fishEncyclopedias) {
		return fishEncyclopediaJpaRepository.save(fishEncyclopedias);
	}
}

package com.backend.domain.fishencyclopediamaxlength.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishencyclopediamaxlength.entity.FishEncyclopediaMaxLength;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaMaxLengthRepositoryImpl implements FishEncyclopediaMaxLengthRepository {

	private final FishEncyclopediaMaxLengthJpaRepository fishEncyclopediaMaxLengthJpaRepository;

	@Override
	public FishEncyclopediaMaxLength save(
		final FishEncyclopediaMaxLength fishEncyclopediaMaxLength
	) {
		return fishEncyclopediaMaxLengthJpaRepository.save(fishEncyclopediaMaxLength);
	}

	@Override
	public Optional<FishEncyclopediaMaxLength> findByFishIdAndMemberId(
		final Long fishId,
		final Long memberId
	) {
		return fishEncyclopediaMaxLengthJpaRepository.findByFishIdAndMemberId(fishId, memberId);
	}
}

package com.backend.domain.fishingtrippost.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostRepositoryImpl implements FishingTripPostRepository {

	private final FishingTripPostJpaRepository fishingTripPostJpaRepository;

	@Override
	public FishingTripPost save(final FishingTripPost fishingTripPost) {
		return fishingTripPostJpaRepository.save(fishingTripPost);
	}

	@Override
	public Optional<FishingTripPost> findById(final Long fishingTripPostId) {
		return fishingTripPostJpaRepository.findById(fishingTripPostId);
	}
}

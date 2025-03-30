package com.backend.domain.shipfishingpost.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostRepositoryImpl implements ShipFishingPostRepository {

	private final ShipFishingPostJpaRepository shipFishingPostJpaRepository;
	private final ShipFishingPostQueryRepository shipFishingPostQueryRepository;

	@Override
	public ShipFishingPost save(final ShipFishingPost shipFishingPost) {

		return shipFishingPostJpaRepository.save(shipFishingPost);
	}

	@Override
	public Optional<ShipFishingPostResponse.Detail> findDetailById(final Long fishingPostId) {

		return shipFishingPostQueryRepository.findDetailById(fishingPostId);
	}

}
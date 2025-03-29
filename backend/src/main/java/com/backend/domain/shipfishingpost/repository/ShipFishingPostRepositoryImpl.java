package com.backend.domain.shipfishingpost.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostRepositoryImpl implements ShipFishingPostRepository {

	private final ShipFishingPostJpaRepository shipFishingPostJpaRepository;
	private final ShipFishingPostQueryRepository shipFishingPostQueryRepository;

	@Override
	public ShipFishingPost save(ShipFishingPost shipFishingPost) {

		return shipFishingPostJpaRepository.save(shipFishingPost);
	}

}
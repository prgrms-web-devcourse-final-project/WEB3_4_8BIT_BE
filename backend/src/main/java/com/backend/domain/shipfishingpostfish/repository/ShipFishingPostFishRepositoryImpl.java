package com.backend.domain.shipfishingpostfish.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostFishRepositoryImpl implements ShipFishingPostFishRepository {

	private final ShipFishingPostFishJpaRepository shipFishingPostFishJpaRepository;
	private final ShipFishingPostFishQueryRepository shipFishingPostFishQueryRepository;

	@Override
	public ShipFishingPostFish save(final ShipFishingPostFish shipFishingPostFish) {

		return shipFishingPostFishJpaRepository.save(shipFishingPostFish);
	}

	@Override
	public void saveAll(final List<ShipFishingPostFish> shipFishingPostFishList) {

		shipFishingPostFishJpaRepository.saveAll(shipFishingPostFishList);
	}

	@Override
	public List<ShipFishingPostFish> findAll() {

		return shipFishingPostFishJpaRepository.findAll();
	}

	@Override
	public void saveAllByBulkQuery(final List<ShipFishingPostFish> shipFishingPostFishList, final int batchSize) {
		shipFishingPostFishQueryRepository.batchInsert(shipFishingPostFishList, batchSize);
	}
}

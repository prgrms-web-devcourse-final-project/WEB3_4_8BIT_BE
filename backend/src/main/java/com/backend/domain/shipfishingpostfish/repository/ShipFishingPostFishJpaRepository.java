package com.backend.domain.shipfishingpostfish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;
import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFishId;

public interface ShipFishingPostFishJpaRepository extends JpaRepository<ShipFishingPostFish, ShipFishingPostFishId> {

}

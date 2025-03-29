package com.backend.domain.shipfishingpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public interface ShipFishingPostJpaRepository extends JpaRepository<ShipFishingPost, Long> {

}
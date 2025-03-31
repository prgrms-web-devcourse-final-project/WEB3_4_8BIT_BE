package com.backend.domain.fishencyclopediamaxlength.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishencyclopediamaxlength.entity.FishEncyclopediaMaxLength;

public interface FishEncyclopediaMaxLengthJpaRepository extends JpaRepository<FishEncyclopediaMaxLength, Long> {

	Optional<FishEncyclopediaMaxLength> findByFishIdAndMemberId(Long fishId, Long memberId);
}

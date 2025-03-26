package com.backend.domain.fishencyclopedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;

public interface FishEncyclopediaJpaRepository extends JpaRepository<FishEncyclopedias, Long> {
}

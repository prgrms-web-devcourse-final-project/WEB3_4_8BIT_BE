package com.backend.domain.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.region.entity.Region;

public interface RegionJpaRepository extends JpaRepository<Region, Long> {
}

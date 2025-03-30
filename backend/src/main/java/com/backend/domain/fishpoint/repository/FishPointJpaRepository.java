package com.backend.domain.fishpoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishpoint.entity.FishPoint;

public interface FishPointJpaRepository extends JpaRepository<FishPoint, Long> {
}

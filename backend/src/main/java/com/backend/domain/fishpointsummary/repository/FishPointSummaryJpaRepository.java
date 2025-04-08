package com.backend.domain.fishpointsummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;

public interface FishPointSummaryJpaRepository extends JpaRepository<FishPointSummary, Long> {
}

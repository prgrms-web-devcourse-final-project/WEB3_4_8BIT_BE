package com.backend.domain.fishpointsummary.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;

public interface FishPointSummaryJpaRepository extends JpaRepository<FishPointSummary, Long> {

	List<FishPointSummary> findByFishPointIdInAndFishIdIn(final Set<Long> fishPointIdList, final Set<Long> fishIdList);
}

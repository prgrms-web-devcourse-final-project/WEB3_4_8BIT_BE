package com.backend.domain.region.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.region.entity.Region;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepository {

	private final RegionJpaRepository regionJpaRepository;

	@Override
	public List<Region> findAll() {
		return regionJpaRepository.findAll();
	}
}

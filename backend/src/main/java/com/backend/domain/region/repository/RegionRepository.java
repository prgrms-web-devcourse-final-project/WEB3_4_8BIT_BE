package com.backend.domain.region.repository;

import java.util.List;

import com.backend.domain.region.entity.Region;

public interface RegionRepository {

	/**
	 * 모든 지역 정보를 조회
	 *
	 * @return 지역의 기본 정보를 담은 리스트
	 */
	List<Region> findAll();
}

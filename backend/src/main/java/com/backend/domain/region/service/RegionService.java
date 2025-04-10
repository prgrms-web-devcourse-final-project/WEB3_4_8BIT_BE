package com.backend.domain.region.service;

import static com.backend.domain.region.dto.response.RegionResponse.*;

import java.util.List;

public interface RegionService {

	/**
	 * 모든 지역 정보를 조회
	 *
	 * @return 지역 정보 리스트
	 */
	List<Basic> getAllRegions();
}

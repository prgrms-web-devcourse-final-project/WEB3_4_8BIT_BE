package com.backend.domain.fishpointsummary.service;

import java.util.List;

import com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.Basic;

public interface FishPointSummaryService {

	/**
	 * 주어진 낚시 포인트 ID에 해당하는 요약 정보 목록을 조회
	 *
	 * @param fishPointId 조회할 낚시 포인트의 ID
	 * @return 해당 낚시 포인트와 연관된 어종의 요약 정보 리스트
	 */
	List<Basic> getFishPointSummaries(final Long fishPointId);
}

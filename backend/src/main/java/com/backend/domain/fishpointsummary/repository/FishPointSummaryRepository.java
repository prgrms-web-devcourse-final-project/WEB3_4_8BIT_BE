package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.List;

public interface FishPointSummaryRepository {

	/**
	 * 지정가 된 낚시 포인트(fishPointId)에서 총 잡힌 마릿수(totalCount)많은 어종 상위 4개를 조회
	 *
	 * @param fishPointId 조회할 낚시 포인트 ID
	 * @return 해당 낚시 포인트에서 가장 많이 잡힌 어종 요약 정보 최대 4건 (없으면 빈 리스트 반환)
	 */
	List<Basic> findTop4ByFishPointIdOrderByTotalCountDesc(Long fishPointId);
}

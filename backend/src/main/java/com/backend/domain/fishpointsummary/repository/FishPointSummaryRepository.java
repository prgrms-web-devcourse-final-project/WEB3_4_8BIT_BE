package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;

import java.util.List;
import java.util.Set;

import com.backend.domain.fishpointsummary.entity.FishPointSummary;

public interface FishPointSummaryRepository {

	/**
	 * 지정가 된 낚시 포인트(fishPointId)에서 총 잡힌 마릿수(totalCount)많은 어종 상위 4개를 조회
	 *
	 * @param fishPointId 조회할 낚시 포인트 ID
	 * @return 해당 낚시 포인트에서 가장 많이 잡힌 어종 요약 정보 최대 4건 (없으면 빈 리스트 반환)
	 */
	List<Basic> findTop4ByFishPointIdOrderByTotalCountDesc(final Long fishPointId);

	/**
	 * 주어진 낚시 포인트 ID 리스트와 물고기 ID 리스트에 해당하는 요약 정보를 조회
	 *
	 * @param fishPointIdList 조회할 낚시 포인트 ID 리스트
	 * @param fishIdList 조회할 물고기 ID 리스트
	 * @return 조건에 일치하는 {@link FishPointSummary} 리스트
	 */
	List<FishPointSummary> findByFishPointIdInAndFishIdIn(final Set<Long> fishPointIdList, final Set<Long> fishIdList);

	/**
	 * 낚시 포인트 요약 정보 리스트를 일괄 저장
	 *
	 * @param fishPointSummaryList 저장할 {@link FishPointSummary} 리스트
	 */
	void saveAll(final List<FishPointSummary> fishPointSummaryList);
}

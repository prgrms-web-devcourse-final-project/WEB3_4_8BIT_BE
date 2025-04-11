package com.backend.domain.activityhistory.repository;

import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface ActivityHistoryRepository {

	/**
	 * 활동 내역 저장 메소드
	 *
	 * @param activityHistory {@link ActivityHistory}
	 * @return {@link ActivityHistory}
	 * @implSpec 활동 내역을 저장 후 저장한 데이터 반환
	 */
	ActivityHistory save(final ActivityHistory activityHistory);

	/**
	 * 활동 내역 검색 메소드
	 *
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @param requestDto       {@link ActivityHistoryRequest.Search}
	 * @param memberId         {@link Long}
	 * @return {@link ScrollResponse<ActivityHistoryResponse.Detail>}
	 * @implSpec memberId와 활동 타입이 일차하는 데이터를 조회 후 결과 값 반환
	 */
	ScrollResponse<ActivityHistoryResponse.Detail> findDetail(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	);
}

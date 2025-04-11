package com.backend.domain.activityhistory.repository;

import java.util.List;

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

	/**
	 * 현재 날짜 기준 1달 전까지의 데이터 ID 값을 조회하는 메소드 입니다.
	 *
	 * @return {@link List<Long>}
	 * @implSpec 현재 날짜 기준 1달 전까지의 데이터의 ID 값을 조회 후 결과 값 반환
	 */
	List<Long> findActivityHistoryIdsBeforeOneMonth();

	/**
	 * 파라미터로 받은 List에 해당하는 ID의 데이터를 삭제하는 메소드 입니다.
	 *
	 * @param activityHistoryidList {@link List<Long>}
	 * @return {@link Long} 삭제된 데이터 개수
	 * @implSpec 파라미터로 받은 List에 해당하는 ID의 데이터를 삭제
	 */
	long deleteByIdList(final List<Long> activityHistoryidList);
}

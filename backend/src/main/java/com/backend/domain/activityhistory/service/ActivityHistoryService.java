package com.backend.domain.activityhistory.service;

import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface ActivityHistoryService {

	/**
	 * 활동 내역 저장 메소드
	 *
	 * @param target {@link Object} 저장했던 엔티티
	 * @implSpec 엔티티를 받아서 지원하는 타입이면 활동 내역 엔티티 생성 후 저장
	 * @author Kim Dong O
	 */
	void createActivityHistory(
		final Object target
	);

	/**
	 * 활동 내역 검색 메소드
	 *
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @param requestDto       {@link ActivityHistoryRequest.Search}
	 * @param memberId         {@link Long}
	 * @return {@link ScrollResponse < ActivityHistoryResponse.Detail>}
	 * @implSpec memberId와 활동 타입이 일차하는 데이터를 조회 후 결과 값 반환
	 */
	ScrollResponse<ActivityHistoryResponse.Detail> getDetailList(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	);
}

package com.backend.domain.activityhistory.repository;

import com.backend.domain.activityhistory.entity.ActivityHistory;

public interface ActivityHistoryRepository {

	/**
	 * 활동 내역 저장 메소드
	 * @param activityHistory {@link ActivityHistory}
	 * @return {@link ActivityHistory}
	 * @implSpec 활동 내역을 저장 후 저장한 데이터 반환
	 */
	ActivityHistory save(final ActivityHistory activityHistory);
}

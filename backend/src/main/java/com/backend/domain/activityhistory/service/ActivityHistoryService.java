package com.backend.domain.activityhistory.service;

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
}

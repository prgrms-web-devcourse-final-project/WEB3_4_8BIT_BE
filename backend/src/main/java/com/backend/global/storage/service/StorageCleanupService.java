package com.backend.global.storage.service;

import java.time.Duration;

public interface StorageCleanupService {

	/**
	 * 업로드되지 않은 파일 중 일정 시간 이상 지난 파일을 삭제
	 *
	 * @param olderThan 삭제 기준 시간 (예: 10분 이상)
	 */
	void deletePendingFiles(final Duration olderThan);
}

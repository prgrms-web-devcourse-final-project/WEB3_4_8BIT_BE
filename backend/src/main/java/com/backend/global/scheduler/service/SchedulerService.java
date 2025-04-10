package com.backend.global.scheduler.service;

import com.backend.global.scheduler.entity.SchedulerStatus;

public interface SchedulerService {

	/**
	 * 스케줄러 작업 내용을 조회하는 메서드
	 * @param jobName 작업 이름
	 * @return {@link SchedulerStatus}
	 * @implSpec 스케쥴러 작업 내용을 조회합니다.
	 * @author swjoon
	 */
	SchedulerStatus getSchedulerStatus(final String jobName);

}

package com.backend.global.scheduler.repository;

import java.util.Optional;

import com.backend.global.scheduler.entity.SchedulerStatus;

public interface SchedulerRepository {

	SchedulerStatus save(final SchedulerStatus schedulerStatus);

	Optional<SchedulerStatus> findByJobName(final String jobName);

}

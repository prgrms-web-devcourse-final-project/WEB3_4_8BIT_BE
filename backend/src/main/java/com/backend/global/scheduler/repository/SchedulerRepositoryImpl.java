package com.backend.global.scheduler.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.global.scheduler.entity.SchedulerStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SchedulerRepositoryImpl implements SchedulerRepository {

	private final SchedulerJpaRepository schedulerJpaRepository;

	@Override
	public SchedulerStatus save(final SchedulerStatus schedulerStatus) {
		return schedulerJpaRepository.save(schedulerStatus);
	}

	@Override
	public Optional<SchedulerStatus> findByJobName(final String jobName) {
		return schedulerJpaRepository.findByJobName(jobName);
	}
}

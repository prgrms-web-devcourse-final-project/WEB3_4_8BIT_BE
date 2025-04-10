package com.backend.global.scheduler.service;

import org.springframework.stereotype.Service;

import com.backend.global.scheduler.entity.SchedulerStatus;
import com.backend.global.scheduler.repository.SchedulerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

	private final SchedulerRepository schedulerRepository;

	@Override
	public SchedulerStatus getSchedulerStatus(final String jobName) {
		return schedulerRepository.findByJobName(jobName)
			.orElseGet(() -> schedulerRepository.save(SchedulerStatus.builder().jobName(jobName).build()));
	}
}

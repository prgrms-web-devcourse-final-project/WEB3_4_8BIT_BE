package com.backend.global.scheduler.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.global.scheduler.entity.SchedulerStatus;

public interface SchedulerJpaRepository extends JpaRepository<SchedulerStatus, String> {

	Optional<SchedulerStatus> findByJobName(final String jobName);

}

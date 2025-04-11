package com.backend.domain.activityhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.activityhistory.entity.ActivityHistory;

public interface ActivityHistoryJpaRepository extends JpaRepository<ActivityHistory, Long> {
}

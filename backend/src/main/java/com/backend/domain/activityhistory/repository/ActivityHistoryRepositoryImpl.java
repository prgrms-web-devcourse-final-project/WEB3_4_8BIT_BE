package com.backend.domain.activityhistory.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.activityhistory.entity.ActivityHistory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ActivityHistoryRepositoryImpl implements ActivityHistoryRepository {

	private final ActivityHistoryJpaRepository activityHistoryJpaRepository;

	@Override
	public ActivityHistory save(final ActivityHistory activityHistory) {
		return activityHistoryJpaRepository.save(activityHistory);
	}
}

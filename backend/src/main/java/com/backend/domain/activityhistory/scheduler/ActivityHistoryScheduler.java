package com.backend.domain.activityhistory.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.activityhistory.repository.ActivityHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityHistoryScheduler {
	private final ActivityHistoryRepository activityHistoryRepository;

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteActivityHistoriesOlderThanOneMonth() {
		List<Long> activityHistoryIdsBeforeOneMonthList = activityHistoryRepository
			.findActivityHistoryIdsBeforeOneMonth();

		long deletedCount = activityHistoryRepository.deleteByIdList(activityHistoryIdsBeforeOneMonthList);

		log.debug("삭제된 활동 내역 데이터 개수: {}", deletedCount);
	}
}

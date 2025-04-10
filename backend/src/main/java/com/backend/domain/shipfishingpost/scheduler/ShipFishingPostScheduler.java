package com.backend.domain.shipfishingpost.scheduler;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.scheduler.entity.SchedulerStatus;
import com.backend.global.scheduler.service.SchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostScheduler {

	private static final String JOB_NAME = "ShipFishingPostRating";

	private final SchedulerService schedulerService;

	private final ShipFishingPostRepository shipFishingPostRepository;

	@Scheduled(fixedRateString = "60000")
	@Transactional
	public void refreshPostRating() {
		log.debug("게시글 평점 업데이트 시작");

		SchedulerStatus scheduler = schedulerService.getSchedulerStatus(JOB_NAME);

		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime lastRun = scheduler.getLastRun();

		shipFishingPostRepository.updateReviewEverRate(now, lastRun);

		scheduler.setLastRun(now);

		log.debug("게시글 평점 업데이트 완료");
	}
}

package com.backend.domain.fishpointsummary.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.domain.fishpointsummary.service.FishPointSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FishPointSummaryScheduler {

	private final FishPointSummaryService fishPointSummaryService;

	/**
	 * 매시간 정각마다 FishPointSummary 집계 업데이트
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void runUpdateHourlySummary() {
		log.debug("[Scheduler] FishPointSummary 집계 스케줄러 실행 시작");

		try {
			fishPointSummaryService.updateFishPointSummariesHourly();
			log.debug("[Scheduler] FishPointSummary 집계 스케줄러 정상 종료");
		} catch (Exception e) {
			log.error("[Scheduler] 집계 실행 중 예외 발생", e);
		}
	}

}

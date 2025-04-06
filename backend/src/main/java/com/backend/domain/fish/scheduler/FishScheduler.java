package com.backend.domain.fish.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishScheduler {

	private final FishRepository fishRepository;
	private final FishEncyclopediaRepository fishEncyclopediaRepository;

	/**
	 * 매시간마다 인기 점수를 수정하는 스케줄러 메소드 입니다.
	 *
	 * @author Kim Dong O
	 */
	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
	@Transactional
	public void scheduleUpdateFishPopularityScores() {
		List<Tuple> findHourlyFishCountSummaryList = fishEncyclopediaRepository.findHourlyFishCountSummary();

		log.debug("조회된 물고기 수: {}", findHourlyFishCountSummaryList.size());

		fishRepository.updateFishPopularityScores(findHourlyFishCountSummaryList);
	}
}

package com.backend.domain.fish.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.repository.FishRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishScheduler {

	private final FishRepository fishRepository;

	/**
	 * 매시간마다 인기 점수를 수정하는 스케줄러 메소드 입니다.
	 *
	 * @author Kim Dong O
	 */
	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
	@Transactional
	public void scheduleUpdateFishPopularityScores() {
		fishRepository.updateFishPopularityScores();
	}
}

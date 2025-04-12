package com.backend.domain.like.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.like.repository.LikeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 삭제된 좋아요 영구 삭제 스케줄러
 *
 * @implSpec isDeleted = true 좋아요 데이터를 1시간마다 영구 삭제한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeCleanUpScheduler {

	private final LikeRepository likeRepository;

	/**
	 * 매 시간 정각마다 소프트 삭제된 좋아요를 DB 영구 삭제
	 */
	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void cleanUpSoftDeletedLikes() {
		try {
			int deletedCount = likeRepository.deleteAllSoftDeletedLikes();
			log.info("[Like Clean Up] 영구 삭제된 좋아요 개수: {}", deletedCount);
		} catch (Exception e) {
			log.error("[Like Clean Up] 삭제 작업 중 오류 발생", e);
		}
	}

}

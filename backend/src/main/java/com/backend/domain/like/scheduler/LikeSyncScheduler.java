package com.backend.domain.like.scheduler;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 좋아요 수 동기화 스케줄러
 *
 * @implSpec Redis 누적된 좋아요 수를 5분마다 DB에 반영
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeSyncScheduler {

	private final RedisUtil redisUtil;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final FishingTripPostRepository fishingTripPostRepository;

	private static final String PREFIX = "like_count::";

	@Scheduled(cron = "0 */5 * * * *")
	@Transactional
	public void syncLikeCountsFromRedis() {
		Map<String, Integer> likeMap = redisUtil.scanKeysAndValues(PREFIX);

		for (Map.Entry<String, Integer> entry : likeMap.entrySet()) {
			String key = entry.getKey();
			Integer redisLikeCount = entry.getValue();

			if (redisLikeCount == null)
				continue;

			String[] parts = key.split("::");
			if (parts.length != 3) {
				log.warn("[Like 동기화 실패] 잘못된 키 형식: {}", key);
				continue;
			}

			LikeTargetType type = LikeTargetType.valueOf(parts[1]);
			Long targetId = Long.parseLong(parts[2]);

			boolean isUpdated = updateLikeCountToDB(type, targetId, redisLikeCount);

			if (isUpdated) {
				log.debug("[Like 동기화 완료] {} (ID: {}) → {}개", type, targetId, redisLikeCount);
				redisUtil.deleteKeyIfExists(key);
			} else {
				log.warn("[Like 동기화 실패] 존재하지 않는 {} 게시글 (ID: {})", type, targetId);
			}
		}
	}

	/**
	 * Redis 저장된 좋아요 수를 DB에 반영한다.
	 *
	 * @param type           좋아요 대상 타입 (예: SHIP_FISHING_POST, FISHING_TRIP_POST)
	 * @param targetId       좋아요 대상 ID
	 * @param redisLikeCount Redis 저장된 좋아요 수
	 * @return 업데이트가 성공적으로 이루어진 경우 true, 대상 게시글이 존재하지 않아 실패한 경우 false
	 */

	private boolean updateLikeCountToDB(
		final LikeTargetType type,
		final Long targetId,
		final int redisLikeCount
	) {
		return switch (type) {
			case SHIP_FISHING_POST -> shipFishingPostRepository.existsById(targetId)
				&& shipFishingPostRepository.updateLikeCount(targetId, (long)redisLikeCount);
			case FISHING_TRIP_POST -> fishingTripPostRepository.existsById(targetId)
				&& fishingTripPostRepository.updateLikeCount(targetId, (long)redisLikeCount);
		};
	}
}

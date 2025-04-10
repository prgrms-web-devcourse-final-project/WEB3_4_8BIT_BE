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
 * @implSpec Redis 누적된 좋아요 수를 5분마다 DB에 반영한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeSyncScheduler {

	private final RedisUtil redisUtil;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final FishingTripPostRepository fishingTripPostRepository;

	private static final String PREFIX = "like_count::";

	/**
	 * 5분마다 Redis 좋아요 값 DB 반영
	 */
	@Scheduled(cron = "0 */5 * * * *")
	@Transactional
	public void syncLikeCountsFromRedis() {
		Map<String, Integer> likeMap = redisUtil.scanKeysAndValues(PREFIX);

		for (String key : likeMap.keySet()) {
			Integer redisLikeCount = likeMap.get(key);
			if (redisLikeCount == null)
				continue;

			String[] parts = key.split("::");
			if (parts.length != 3)
				continue;

			LikeTargetType type = LikeTargetType.valueOf(parts[1]);
			Long targetId = Long.parseLong(parts[2]);

			boolean isUpdated = switch (type) {
				case SHIP_FISHING_POST ->
					shipFishingPostRepository.updateLikeCount(targetId, redisLikeCount.longValue());
				case FISHING_TRIP_POST ->
					fishingTripPostRepository.updateLikeCount(targetId, redisLikeCount.longValue());
			};

			if (isUpdated) {
				log.debug("[Like 동기화] {} 게시글 (ID: {}) → DB 좋아요 수 = {}", type, targetId, redisLikeCount);
				redisUtil.deleteKeyIfExists(key);
			} else {
				log.warn("[Like 동기화 실패] 존재하지 않는 게시글]");
			}

		}
	}
}

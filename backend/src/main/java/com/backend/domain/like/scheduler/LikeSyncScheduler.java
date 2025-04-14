package com.backend.domain.like.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.service.LikeService;
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

	private static final String PREFIX = "like_count::";
	private final LikeService likeService;

	@Scheduled(cron = "0 */3 * * * *")
	@Transactional
	public void syncLikeCountsFromRedis() {
		log.debug("게시글 좋아요 업데이트 시작");

		Map<String, Long> likeMap = redisUtil.scanKeysAndValues(PREFIX);
		Map<LikeTargetType, List<LikeResponse.LikeSyncDto>> grouped = new HashMap<>();

		for (Map.Entry<String, Long> entry : likeMap.entrySet()) {
			String key = entry.getKey();
			Long redisLikeCount = entry.getValue();

			if (redisLikeCount == null)
				continue;

			String[] parts = key.split("::");
			if (parts.length != 3) {
				log.warn("[Like 동기화 실패] 잘못된 키 형식: {}", key);
				continue;
			}

			LikeTargetType type = LikeTargetType.valueOf(parts[1]);
			Long targetId = Long.parseLong(parts[2]);

			grouped.computeIfAbsent(type, k -> new ArrayList<>())
				.add(new LikeResponse.LikeSyncDto(targetId, redisLikeCount));
		}

		grouped.forEach((type, dtoList) -> {
			try {
				log.info("[Like 동기화 실행] 대상: {}, 건수: {}", type, dtoList.size());
				likeService.updateLikeCounts(type, dtoList);
			} catch (Exception e) {
				log.error("[Like 동기화 실패] 대상: {}", type, e);
			}
		});

		log.debug("게시글 좋아요 업데이트 종료");
	}
}

package com.backend.domain.like.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 좋아요 수 캐싱 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeCacheService {

	private final RedisUtil redisUtil;
	private final LikeRepository likeRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final FishingTripPostRepository fishingTripPostRepository;

	private static final String PREFIX = "like_count::";

	/**
	 * 좋아요 수 조회
	 * 캐시에 값이 있으면 해당 값을 반환하고
	 * 없으면 DB에서 조회 후 캐시에 저장한다.
	 *
	 * @param type     좋아요 대상 타입
	 * @param targetId 좋아요 대상 ID
	 * @return 좋아요 수
	 * @throws ShipFishingPostException 대상이 존재하지 않는 선상낚시 게시글일 경우
	 * @throws FishingTripPostException 대상이 존재하지 않는 동출모집 게시글일 경우
	 */
	@Cacheable(value = "like_count", key = "#type.name() + '::' + #targetId")
	public Long getLikeCount(final LikeTargetType type, final Long targetId) {
		validateLikeTarget(type, targetId);
		return likeRepository.countByTargetTypeAndTargetId(type, targetId);
	}

	/**
	 * 게시글 좋아요 수를 실시간 캐시로 업데이트한다.
	 * - 좋아요를 누르면 캐시 +1
	 * - 좋아요를 취소하면 캐시 -1
	 *
	 * @param type     좋아요 대상 타입 (SHIP_FISHING_POST, FISHING_TRIP_POST)
	 * @param targetId 좋아요 대상 ID
	 * @param isLike   true → 좋아요, false → 좋아요 취소
	 * @return Redis에 반영된 좋아요 수
	 */
	@CachePut(value = "like_count", key = "#type.name() + '::' + #targetId")
	public Long updateLikeCountCache(final LikeTargetType type, final Long targetId, final Boolean isLike) {
		String key = buildKey(type, targetId); // like_count::TYPE::ID
		return isLike ? redisUtil.increment(key) : redisUtil.decrement(key);
	}

	/**
	 * Like 대상 존재 여부 검증
	 *
	 * @param targetType 좋아요 대상 타입
	 * @param targetId   좋아요 대상 ID
	 * @throws ShipFishingPostException 존재하지 않는 선상 낚시 게시글일 경우 예외 발생
	 * @throws FishingTripPostException 존재하지 않는 동출 모집 게시글일 경우 예외 발생
	 */
	private void validateLikeTarget(final LikeTargetType targetType, final Long targetId) {
		boolean exists = switch (targetType) {
			case SHIP_FISHING_POST -> shipFishingPostRepository.existsById(targetId);
			case FISHING_TRIP_POST -> fishingTripPostRepository.existsById(targetId);
		};

		if (!exists) {
			log.warn("[존재하지 않는 좋아요] 타입: {}, ID: {}", targetType, targetId);
			throw switch (targetType) {
				case SHIP_FISHING_POST -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND);
				case FISHING_TRIP_POST ->
					new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND);
			};
		}
	}

	/**
	 * Redis key 생성 (like_count::{type}::{targetId} 형식)
	 *
	 * @param type     좋아요 대상 타입
	 * @param targetId 대상 ID
	 * @return Redis key 문자열
	 */
	private String buildKey(LikeTargetType type, Long targetId) {
		return PREFIX + type.name() + "::" + targetId;
	}
}
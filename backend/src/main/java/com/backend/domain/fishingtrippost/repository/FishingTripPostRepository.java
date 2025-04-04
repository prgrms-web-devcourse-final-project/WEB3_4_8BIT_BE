package com.backend.domain.fishingtrippost.repository;

import java.util.Optional;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;

public interface FishingTripPostRepository {

	/**
	 * 동출 모집 게시글 저장 메서드
	 *
	 * @param fishingTripPost {@link FishingTripPost}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPost 받아서 저장 후 저장된 엔티티 반환
	 */

	FishingTripPost save(final FishingTripPost fishingTripPost);


	/**
	 * 동출 모집 게시글 조회 메서드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPostId 받아서 조회된 동출 모집 게시글 엔티티 반환
	 */
	Optional<FishingTripPost> findById(final Long fishingTripPostId);
}

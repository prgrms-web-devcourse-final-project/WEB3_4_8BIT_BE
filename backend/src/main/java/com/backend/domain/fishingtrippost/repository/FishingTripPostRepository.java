package com.backend.domain.fishingtrippost.repository;

import java.util.Optional;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
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

	/**
	 * 게시글 ID를 기반으로 동출 모집 게시글의 상세 정보를 조회합니다.
	 *
	 * <p>조회된 정보는 응답 DTO {@link FishingTripPostResponse.Detail} 로 변환되어 반환되며,
	 * 내부적으로 포맷팅된 날짜 및 인원 수 형식의 문자열을 포함합니다.</p>
	 *
	 * @param fishingTripPostId 조회할 게시글의 고유 ID
	 * @return 해당 ID의 게시글 상세 정보가 존재할 경우 {@link FishingTripPostResponse.Detail}을 포함한 Optional,
	 *         존재하지 않으면 {@link Optional#empty()}
	 */

	Optional<FishingTripPostResponse.Detail> findDetailById(final Long fishingTripPostId);

	/**
	 * 동출 모집 게시글 조회 메서드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPostId 받아서 조회된 동출 모집 게시글 엔티티 반환
	 */
	Optional<FishingTripPost> findById(final Long fishingTripPostId);
}

package com.backend.domain.shipfishingpost.repository;

import java.util.Optional;

import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public interface ShipFishingPostRepository {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param shipFishingPost {@link ShipFishingPost}
	 * @return {@link ShipFishingPost}
	 * @implSpec 선상 낚시 게시글 저장 메서드 입니다.
	 */
	ShipFishingPost save(final ShipFishingPost shipFishingPost);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.Detail}
	 * @implSpec 선상 낚시 게시글 ID를 받아서 조회 후 ShipFishingPostResponse.Detail 을 반환한다.
	 */
	Optional<ShipFishingPostResponse.Detail> findDetailById(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드 (게시글, 멤버, 선박 정보 포함)
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.DetailAll}
	 * @implSpec 선상 낚시 게시글 ID를 받아서 조회 후 ShipFishingPostResponse.DetailAll 을 반환한다.
	 */
	Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId);
}
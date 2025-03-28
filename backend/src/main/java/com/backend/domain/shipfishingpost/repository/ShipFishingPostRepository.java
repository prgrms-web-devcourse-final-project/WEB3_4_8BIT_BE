package com.backend.domain.shipfishingpost.repository;

import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public interface ShipFishingPostRepository {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param shipFishingPost {@link ShipFishingPost}
	 * @return {@link ShipFishingPost}
	 * @implSpec 선상 낚시 게시글 저장 메서드 입니다.
	 */
	ShipFishingPost save(ShipFishingPost shipFishingPost);

}
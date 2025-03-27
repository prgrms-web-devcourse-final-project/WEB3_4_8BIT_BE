package com.backend.domain.shipfishposts.repository;

import com.backend.domain.shipfishposts.entity.ShipFishPosts;

public interface ShipFishPostsRepository {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param shipFishPosts {@link ShipFishPosts}
	 * @return {@link ShipFishPosts}
	 * @implSpec 선상 낚시 게시글 저장 메서드 입니다.
	 */
	ShipFishPosts save(ShipFishPosts shipFishPosts);

}
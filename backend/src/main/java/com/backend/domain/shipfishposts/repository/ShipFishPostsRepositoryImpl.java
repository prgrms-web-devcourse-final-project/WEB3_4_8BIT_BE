package com.backend.domain.shipfishposts.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishposts.entity.ShipFishPosts;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishPostsRepositoryImpl implements ShipFishPostsRepository {

	private final ShipFishPostsJpaRepository shipFishPostsJpaRepository;
	private final ShipFishPostsQueryRepository shipFishPostsQueryRepository;

	@Override
	public ShipFishPosts save(ShipFishPosts shipFishPosts) {

		return shipFishPostsJpaRepository.save(shipFishPosts);
	}

}
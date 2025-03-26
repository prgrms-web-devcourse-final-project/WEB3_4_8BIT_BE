package com.backend.domain.shipfishposts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.shipfishposts.entity.ShipFishPosts;

public interface ShipFishPostsJpaRepository extends JpaRepository<ShipFishPosts, Long> {

}
package com.backend.domain.shipfishposts.repository;

import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipFishPostsRepository extends JpaRepository<ShipFishPosts, Long>, ShipFIshPostsRepositoryCustom {



}

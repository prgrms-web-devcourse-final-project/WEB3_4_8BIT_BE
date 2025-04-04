package com.backend.domain.shipfishingpostfish.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostFishQueryRepository {

	private final JdbcTemplate jdbcTemplate;

	public void batchInsert(final List<ShipFishingPostFish> shipFishingPostFishList, final int batchSize) {
		String sql = "INSERT INTO  ship_fish_post_fishes" +
			"(ship_fishing_post_id, fish_id, created_at, modified_at) " +
			"VALUES (?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, shipFishingPostFishList, batchSize, (ps, shipFishingPostFish) -> {
			ps.setLong(1, shipFishingPostFish.getShipFishingPostId());
			ps.setLong(2, shipFishingPostFish.getFishId());
			ps.setTimestamp(3, java.sql.Timestamp.from(shipFishingPostFish.getCreatedAt().toInstant()));
			ps.setTimestamp(4, java.sql.Timestamp.from(shipFishingPostFish.getModifiedAt().toInstant()));
		});
	}

}

package com.backend.domain.shipfishingpostfish.repository;

import java.util.List;

import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;

public interface ShipFishingPostFishRepository {

	/**
	 * 선상낚시 게시글과 매칭되는 물고기 정보를 저장하는 메서드
	 *
	 * @param shipFishingPostFish {@link List<ShipFishingPostFish>}
	 * @implSpec 선상낚시 게시글과 매칭되는 물고기 정보를 저장한다.
	 */
	ShipFishingPostFish save(final ShipFishingPostFish shipFishingPostFish);

	/**
	 * 선상낚시 게시글과 매칭되는 물고기 정보 List 를 저장하는 메서드
	 *
	 * @param shipFishingPostFishes {@link List<ShipFishingPostFish>}
	 * @implSpec 선상낚시 게시글과 매칭되는 여러 물고기 정보를 한번에 저장한다.
	 */
	void saveAll(final List<ShipFishingPostFish> shipFishingPostFishes);

	/**
	 * 선상낚시 게시글과 매칭되는 물고기 정보 전체 조회 메서드
	 *
	 * @return {@link List<ShipFishingPostFish>}
	 * @implSpec 선상낚시 게시글과 매칭된 전체 물고기 정보를 한번에 조회한다.
	 */
	List<ShipFishingPostFish> findAll();

	/**
	 * 여러건의 예약 일자를 쿼리 한번에 저장하는 메서드입니다.
	 *
	 * @param shipFishingPostFishList {@link List<ShipFishingPostFish>}
	 * @param batchSize {@link Integer}
	 * @implSpec 배치 사이즈를 정해서 여러건의 데이터를 한번에 저장합니다.
	 * @author swjoon
	 */
	void saveAllByBulkQuery(final List<ShipFishingPostFish> shipFishingPostFishList, final int batchSize);
}
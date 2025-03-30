package com.backend.domain.ship.repository;

import java.util.Optional;

import com.backend.domain.ship.entity.Ship;

public interface ShipRepository {

	/**
	 * 선박 저장 메서드
	 *
	 * @param ship {@link Ship}
	 * @return Ship {@link Ship}
	 * @implSpec 선박 정보 저장 메서드 입니다.
	 */
	Ship save(final Ship ship);

	/**
	 * 선박 Id로 선박 정보 조회 메서드
	 *
	 * @param shipId 선박 Id
	 * @return {@link Ship}
	 * @implSpec 선박 정보 조회 메서드 입니다.
	 */
	Optional<Ship> findById(final Long shipId);
}

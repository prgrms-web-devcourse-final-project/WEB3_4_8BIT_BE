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
	 * @author swjoon
	 */
	Ship save(final Ship ship);

	/**
	 * 선박 Id로 선박 정보 조회 메서드
	 *
	 * @param shipId 선박 Id
	 * @return {@link Ship}
	 * @implSpec 선박 정보 조회 메서드 입니다.
	 * @author swjoon
	 */
	Optional<Ship> findById(final Long shipId);

	/**
	 * 회원 ID로 등록된 선박 개수 조회 메서드
	 *
	 * @param memberId 회원 ID
	 * @return {@link Long}
	 * @implSpec 회원 ID로 등록된 선박 개수 조회 후 결과 값 반환
	 * @author Kim Dong O
	 */
	Long countByMemberId(final Long memberId);
}

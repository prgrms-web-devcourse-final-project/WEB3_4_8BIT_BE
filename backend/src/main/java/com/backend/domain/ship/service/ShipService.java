package com.backend.domain.ship.service;

import java.util.List;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;

public interface ShipService {

	/**
	 * 선박 저장 메서드
	 *
	 * @param memberId {@link Long}
	 * @param requestDto {@link ShipRequest.Form}
	 * @return {@link Long}
	 * @implSpec 선박 정보 저장 메서드 입니다.
	 */
	Long createShip(final Long memberId, final ShipRequest.Form requestDto);

	/**
	 * 로그인한 회원의 선박 전체 조회 메서드
	 *
	 * @param memberId {@link Long}
	 * @return {@link Long}
	 * @implSpec 로그인한 회원의 선박 전체 조회 메서드 입니다.
	 */
	List<ShipResponse.Detail> getDetailAll(final Long memberId);

	/**
	 * 선박 수정 메소드
	 *
	 * @param shipId {@link Long}
	 * @param memberId {@link Long}
	 * @param requestDto {@link ShipRequest.Form}
	 * @return {@link Long}
	 * @implSpec 선박 수정 메서드 입니다.
	 */
	Long updateShip(final Long shipId, final Long memberId, final ShipRequest.Form requestDto);
}

package com.backend.domain.ship.service;

import com.backend.domain.ship.dto.request.ShipRequest;

public interface ShipService {

	/**
	 * 선박 저장 메서드
	 *
	 * @param memberId {@link Long}
	 * @param requestDto {@link ShipRequest.Create}
	 * @return {@link Long}
	 * @implSpec 선박 정보 저장 메서드 입니다.
	 */
	Long createShip(final Long memberId, final ShipRequest.Create requestDto);
}

package com.backend.domain.ship.service;

import org.springframework.stereotype.Service;

import com.backend.domain.ship.converter.ShipConverter;
import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.repository.ShipRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

	private final ShipRepository shipRepository;
	private static final Long MAX_SHIPS_PER_MEMBER = 5L;

	@Override
	public Long createShip(final Long memberId, final ShipRequest.Create requestDto) {

		Long countByMemberId = shipRepository.countByMemberId(memberId);

		if (countByMemberId > MAX_SHIPS_PER_MEMBER) {
			throw new ShipException(ShipErrorCode.MAX_SHIP_COUNT_EXCEEDED);
		}

		Ship ship = ShipConverter.fromCreate(memberId, requestDto);

		Ship savedShip = shipRepository.save(ship);

		log.debug("선박 저장: {}", savedShip);

		return savedShip.getShipId();
	}
}

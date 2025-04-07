package com.backend.domain.ship.service;

import org.springframework.stereotype.Service;

import com.backend.domain.ship.converter.ShipConverter;
import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.repository.ShipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

	private final ShipRepository shipRepository;

	@Override
	public Long createShip(final Long memberId, final ShipRequest.Create requestDto) {

		//TODO 추후 최대 몇개까지 등록 허용할건지 정해야함

		Ship ship = ShipConverter.fromCreate(memberId, requestDto);

		return shipRepository.save(ship).getShipId();
	}
}

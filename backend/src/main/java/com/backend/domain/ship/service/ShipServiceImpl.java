package com.backend.domain.ship.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.ship.converter.ShipConverter;
import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
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
	public Long createShip(final Long memberId, final ShipRequest.Form requestDto) {

		Long countByMemberId = shipRepository.countByMemberId(memberId);

		log.debug("{}번 회원의 저장된 선박 개수: {}", memberId, countByMemberId);

		validMaxShipLimit(countByMemberId);

		Ship ship = ShipConverter.fromCreate(memberId, requestDto);

		Ship savedShip = shipRepository.save(ship);

		log.debug("선박 저장: {}", savedShip);

		return savedShip.getShipId();
	}

	@Override
	public List<ShipResponse.Detail> getDetailAll(final Long memberId) {

		List<ShipResponse.Detail> getShipAllList = shipRepository.findDetailAll(memberId);

		log.debug("선박 전체 조회: {}", getShipAllList);

		return getShipAllList;
	}

	@Override
	@Transactional
	public Long updateShip(
		final Long shipId,
		final Long memberId,
		final ShipRequest.Form requestDto
	) {

		Ship findShip = getShip(shipId);

		log.debug("선박 조회: {}", findShip);

		validMemberId(memberId, findShip.getMemberId());

		findShip.updateShip(
			requestDto.shipName(),
			requestDto.shipNumber(),
			requestDto.departurePort(),
			requestDto.passengerCapacity(),
			requestDto.restroomType(),
			requestDto.loungeArea(),
			requestDto.kitchenFacility(),
			requestDto.fishingChair(),
			requestDto.passengerInsurance(),
			requestDto.fishingGearRental(),
			requestDto.mealProvided(),
			requestDto.parkingAvailable()
		);

		return findShip.getShipId();
	}

	private void validMemberId(Long shipMemberId, Long memberId) {

		if (!shipMemberId.equals(memberId)) {
			throw new ShipException(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR);
		}
	}

	private Ship getShip(Long shipId) {
		return shipRepository.findById(shipId)
			.orElseThrow(() -> new ShipException(ShipErrorCode.SHIP_NOT_FOUND));
	}

	private void validMaxShipLimit(final Long countByMemberId) {

		if (countByMemberId > MAX_SHIPS_PER_MEMBER) {
			throw new ShipException(ShipErrorCode.MAX_SHIP_COUNT_EXCEEDED);
		}
	}
}

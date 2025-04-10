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
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

	private final ShipRepository shipRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
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

		Ship getShip = getShip(shipId);

		log.debug("선박 조회: {}", getShip);

		validMemberId(getShip.getMemberId(), memberId);

		getShip.updateShip(
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

		return getShip.getShipId();
	}

	@Override
	public void deleteById(final Long shipId, final Long memberId) {
		Ship getShip = getShip(shipId);
		validMemberId(getShip.getMemberId(), memberId);

		boolean getExistsShipFishingPost = getExistsShipFishingPost(getShip.getShipId());

		validExistsShipFishingPost(getExistsShipFishingPost);

		shipRepository.deleteByShipId(shipId);
	}

	private void validExistsShipFishingPost(final boolean getExistsShipFishingPost) {
		if (getExistsShipFishingPost) {
			throw new ShipException(ShipErrorCode.SHIP_IN_USE_BY_FISHING_POST);
		}
	}

	private Boolean getExistsShipFishingPost(final Long shipId) {
		return shipFishingPostRepository.existsByShipId(shipId);
	}

	private void validMemberId(final Long shipMemberId, final Long memberId) {

		if (!shipMemberId.equals(memberId)) {
			throw new ShipException(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR);
		}
	}

	private Ship getShip(final Long shipId) {
		return shipRepository.findById(shipId)
			.orElseThrow(() -> new ShipException(ShipErrorCode.SHIP_NOT_FOUND));
	}

	private void validMaxShipLimit(final Long countByMemberId) {

		if (countByMemberId > MAX_SHIPS_PER_MEMBER) {
			throw new ShipException(ShipErrorCode.MAX_SHIP_COUNT_EXCEEDED);
		}
	}
}

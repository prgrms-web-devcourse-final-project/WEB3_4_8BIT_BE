package com.backend.domain.ship.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipRepositoryImpl implements ShipRepository {

	private final ShipJpaRepository shipJpaRepository;
	private final ShipQueryRepository shipQueryRepository;

	@Override
	public Ship save(final Ship ship) {
		
		return shipJpaRepository.save(ship);
	}

	@Override
	public Optional<Ship> findById(final Long shipId) {

		return shipJpaRepository.findById(shipId);
	}

	@Override
	public Long countByMemberId(final Long memberId) {
		return shipQueryRepository.countByMemberId(memberId);
	}

	@Override
	public List<ShipResponse.Detail> findDetailAll(final Long memberId) {
		return shipQueryRepository.findDetailAll(memberId);
	}

	@Override
	public void deleteByShipId(Long shipId) {
		shipJpaRepository.deleteById(shipId);
	}
}

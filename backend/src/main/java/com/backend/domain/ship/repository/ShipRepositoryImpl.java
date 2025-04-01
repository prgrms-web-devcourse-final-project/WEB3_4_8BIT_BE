package com.backend.domain.ship.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.ship.entity.Ship;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipRepositoryImpl implements ShipRepository {

	private final ShipJpaRepository shipJpaRepository;

	@Override
	public Ship save(final Ship ship) {
		
		return shipJpaRepository.save(ship);
	}

	@Override
	public Optional<Ship> findById(final Long shipId) {

		return shipJpaRepository.findById(shipId);
	}

}

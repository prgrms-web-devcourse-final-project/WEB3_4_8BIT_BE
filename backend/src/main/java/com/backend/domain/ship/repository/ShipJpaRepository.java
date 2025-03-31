package com.backend.domain.ship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.ship.entity.Ship;

public interface ShipJpaRepository extends JpaRepository<Ship, Long> {

}

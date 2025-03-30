package com.backend.domain.fish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fish.entity.Fish;

public interface FishJpaRepository extends JpaRepository<Fish, Long> {
}

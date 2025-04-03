package com.backend.domain.fishingtrippost.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;

public interface FishingTripPostJpaRepository extends JpaRepository<FishingTripPost, Long> {
}

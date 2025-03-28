package com.backend.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.review.entity.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
}

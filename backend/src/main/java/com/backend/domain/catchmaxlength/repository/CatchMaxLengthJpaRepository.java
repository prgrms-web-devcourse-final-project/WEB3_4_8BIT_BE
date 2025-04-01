package com.backend.domain.catchmaxlength.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;

public interface CatchMaxLengthJpaRepository extends JpaRepository<CatchMaxLength, Long> {

	Optional<CatchMaxLength> findByFishIdAndMemberId(Long fishId, Long memberId);
}

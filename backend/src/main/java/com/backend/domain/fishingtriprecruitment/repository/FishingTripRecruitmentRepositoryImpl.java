package com.backend.domain.fishingtriprecruitment.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripRecruitmentRepositoryImpl implements FishingTripRecruitmentRepository {

	private final FishingTripRecruitmentJpaRepository fishingTripRecruitmentJpaRepository;
	private final FishingTripRecruitmentQueryRepository fishingTripRecruitmentQueryRepository;

	@Override
	public FishingTripRecruitment save(final FishingTripRecruitment fishingTripRecruitment) {
		return fishingTripRecruitmentJpaRepository.save(fishingTripRecruitment);
	}

	@Override
	public Optional<FishingTripRecruitment> findById(final Long fishingTripRecruitmentId) {
		return fishingTripRecruitmentJpaRepository.findById(fishingTripRecruitmentId);
	}

}

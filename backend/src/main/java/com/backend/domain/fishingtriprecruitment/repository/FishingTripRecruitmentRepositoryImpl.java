package com.backend.domain.fishingtriprecruitment.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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

	@Override
	public ScrollResponse<FishingTripRecruitmentResponse.DetailPage> findDetailPageByFishingTripPostIdAndStatus(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId,
		final RecruitmentStatus status) {
		return fishingTripRecruitmentQueryRepository
			.findDetailPageQueryDtoByIdAndStatus(cursorRequestDto, fishingTripPostId, status);
	}
}

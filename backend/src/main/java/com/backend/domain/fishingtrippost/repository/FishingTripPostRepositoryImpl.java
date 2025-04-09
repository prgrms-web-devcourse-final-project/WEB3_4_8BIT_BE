package com.backend.domain.fishingtrippost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostRepositoryImpl implements FishingTripPostRepository {

	private final FishingTripPostJpaRepository fishingTripPostJpaRepository;
	private final FishingTripPostQueryRepository fishingTripPostQueryRepository;

	@Override
	public FishingTripPost save(final FishingTripPost fishingTripPost) {
		return fishingTripPostJpaRepository.save(fishingTripPost);
	}

	@Override
	public Optional<FishingTripPost> findById(final Long fishingTripPostId) {
		return fishingTripPostJpaRepository.findById(fishingTripPostId);
	}

	@Override
	public Optional<FishingTripPostResponse.DetailQueryDto> findDetailQueryDtoById(final Long fishingTripPostId) {
		return fishingTripPostQueryRepository.findDetailDtoById(fishingTripPostId);
	}

	@Override
	public boolean existsById(final Long fishingTripPostId) {
		return fishingTripPostJpaRepository.existsById(fishingTripPostId);
	}

	@Override
	public List<FishingTripPostResponse.DetailPageQueryDto> findScrollDetailPageDto(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword) {
		return fishingTripPostQueryRepository
			.findScrollDetailPageDto(cursorRequestDto,status,regionId,keyword);
	}
}

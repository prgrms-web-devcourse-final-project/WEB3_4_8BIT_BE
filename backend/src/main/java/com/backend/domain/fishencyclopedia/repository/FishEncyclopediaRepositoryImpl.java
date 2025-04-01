package com.backend.domain.fishencyclopedia.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaRepositoryImpl implements FishEncyclopediaRepository {
	private final FishEncyclopediaJpaRepository fishEncyclopediaJpaRepository;
	private final FishEncyclopediaQueryRepository fishEncyclopediaQueryRepository;

	@Override
	public FishEncyclopedia createFishEncyclopedia(final FishEncyclopedia fishEncyclopedia) {
		return fishEncyclopediaJpaRepository.save(fishEncyclopedia);
	}

	@Override
	public ScrollResponse<FishEncyclopediaResponse.Detail> findDetailByAllByFishPointIdAndFishId(
		GlobalRequest.PageRequest pageRequestDto,
		Long fishId,
		Long memberId
	) {
		return fishEncyclopediaQueryRepository.findDetailByAllByFishPointIdAndFishId(pageRequestDto, fishId, memberId);
	}
}

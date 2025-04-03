package com.backend.domain.fishencyclopedia.repository;

import java.util.List;

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
	public ScrollResponse<FishEncyclopediaResponse.Detail> findDetailByAllByMemberIdAndFishId(
		final GlobalRequest.CursorRequest pageRequestDto,
		final Long fishId,
		final Long memberId
	) {

		return fishEncyclopediaQueryRepository.findDetailByAllByMemberIdAndFishId(
			pageRequestDto,
			fishId,
			memberId
		);
	}

	@Override
	public List<FishEncyclopediaResponse.DetailPage> findDetailPageByAllByMemberIdAndFishId(
		final Long memberId
	) {

		return fishEncyclopediaQueryRepository.findDetailPageByAllByMemberIdAndFishId(
			memberId
		);
	}
}

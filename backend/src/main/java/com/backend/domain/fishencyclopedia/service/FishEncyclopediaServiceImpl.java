package com.backend.domain.fishencyclopedia.service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.converter.FishEncyclopediaConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaErrorCode;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpoint.repository.FishPointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishEncyclopediaServiceImpl implements FishEncyclopediaService {

	private final FishEncyclopediaRepository fishEncyclopediaRepository;
	private final FishPointRepository fishPointRepository;
	private final FishRepository fishRepository;

	@Override
	@Transactional
	public Long createFishEncyclopedia(final FishEncyclopediaRequest.Create requestDto, final Long memberId) {
		//Fish, FishPoint 존재하는지 검증
		existsFishId(requestDto.fishId());
		existsFishPointId(requestDto.fishPointId());

		FishEncyclopedia fishEncyclopedia = FishEncyclopediaConverter.fromFishEncyclopediasRequestCreate(
			requestDto,
			memberId
		);

		//TODO 추후 최대 길이 값 검증 추가해야함

		FishEncyclopedia savedFishEncyclopedia = fishEncyclopediaRepository.createFishEncyclopedia(fishEncyclopedia);

		log.debug("물고기 도감 저장: {}", savedFishEncyclopedia);

		return savedFishEncyclopedia.getFishEncyclopediaId();
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<FishEncyclopediaResponse.Detail> getDetailList(
		final FishEncyclopediaRequest.PageRequest requestDto,
		final Long fishId,
		final Long memberId
	) {
		return fishEncyclopediaRepository.findDetailByAllByFishPointIdAndFishId(requestDto, fishId, memberId);
	}

	/**
	 * Fish가 존재하는지 검증하는 메소드 입니다.
	 *
	 * @param fishId
	 * @throws FishEncyclopediaException 물고기가 존재하지 않을 때 발생
	 */
	private void existsFishId(final Long fishId) {

		boolean result = fishRepository.existsById(fishId);

		log.debug("물고기 존재 여부: {}", result);

		if (!result) {
			throw new FishEncyclopediaException(FishEncyclopediaErrorCode.NOT_EXISTS_FISH);
		}
	}

	/**
	 * FishPoint가 존재하는지 검증하는 메소드 입니다.
	 *
	 * @param fishPointId
	 * @throws FishEncyclopediaException 낚시 포인트가 존재하지 않을 때 발생
	 */
	private void existsFishPointId(final Long fishPointId) {

		boolean result = fishPointRepository.existsById(fishPointId);

		log.debug("낚시 포인트 존재 여부: {}", result);

		if (!result) {
			throw new FishEncyclopediaException(FishEncyclopediaErrorCode.NOT_EXISTS_FISH_POINT);
		}
	}
}
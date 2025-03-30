package com.backend.domain.fishencyclopedia.service;

import org.springframework.stereotype.Service;

import com.backend.domain.fishencyclopedia.converter.FishEncyclopediaConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaErrorCode;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishEncyclopediaServiceImpl implements FishEncyclopediaService {

	private final FishEncyclopediaRepository fishEncyclopediaRepository;

	@Override
	public Long save(final FishEncyclopediaRequest.Create create, final Long memberId) {
		//Fish, FishPoint 존재하는지 검증
		//TODO 추후 로직 구현 후 주석 풀 예정
		/*existsFishId(create.fishId());
		existsFishPointId(create.fishPointId());*/

		FishEncyclopedia fishEncyclopedia = FishEncyclopediaConverter.fromFishEncyclopediasRequestCreate(
			create,
			memberId
		);

		FishEncyclopedia savedFishEncyclopedia = fishEncyclopediaRepository.save(fishEncyclopedia);

		return savedFishEncyclopedia.getFishEncyclopediaId();
	}

	/**
	 * Fish가 존재하는지 검증하는 메소드 입니다.
	 *
	 * @param fishId
	 * @throws FishEncyclopediaException 물고기가 존재하지 않을 때 발생
	 */
	private void existsFishId(final Long fishId) {
		boolean result = false;

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
		boolean result = false;

		if (!result) {
			throw new FishEncyclopediaException(FishEncyclopediaErrorCode.NOT_EXISTS_FISH_POINT);
		}
	}
}
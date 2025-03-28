package com.backend.domain.fishencyclopedia.service;

import org.springframework.stereotype.Service;

import com.backend.domain.fishencyclopedia.converter.FishEncyclopediasConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediasRequest;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedias;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaErrorCode;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediasException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.member.entity.Members;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishEncyclopediasServiceImpl implements FishEncyclopediasService {

	private final FishEncyclopediaRepository fishEncyclopediaRepository;

	@Override
	public Long save(FishEncyclopediasRequest.Create create, Members members) {
		//Fish, FishPoint 존재하는지 검증
		//TODO 추후 로직 구현 후 주석 풀 예정
		/*existsFishId(create.fishId());
		existsFishPointId(create.fishPointId());*/

		FishEncyclopedias fishEncyclopedias = FishEncyclopediasConverter.fromFishEncyclopediasRequestCreate(
			create,
			members.getMemberId()
		);

		FishEncyclopedias savedFishEncyclopedias = fishEncyclopediaRepository.save(fishEncyclopedias);

		return savedFishEncyclopedias.getFishEncyclopediaId();
	}

	/**
	 * Fish가 존재하는지 검증하는 메소드 입니다.
	 *
	 * @param fishId
	 * @throws FishEncyclopediasException 물고기가 존재하지 않을 때 발생
	 */
	private void existsFishId(Long fishId) {
		boolean result = false;

		if (!result) {
			throw new FishEncyclopediasException(FishEncyclopediaErrorCode.NOT_EXISTS_FISH);
		}
	}

	/**
	 * FishPoint가 존재하는지 검증하는 메소드 입니다.
	 *
	 * @param fishPointId
	 * @throws FishEncyclopediasException 낚시 포인트가 존재하지 않을 때 발생
	 */
	private void existsFishPointId(Long fishPointId) {
		boolean result = false;

		if (!result) {
			throw new FishEncyclopediasException(FishEncyclopediaErrorCode.NOT_EXISTS_FISH_POINT);
		}
	}
}
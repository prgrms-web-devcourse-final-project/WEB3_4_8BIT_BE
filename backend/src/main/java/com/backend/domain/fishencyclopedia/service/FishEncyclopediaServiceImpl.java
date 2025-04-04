package com.backend.domain.fishencyclopedia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.catchmaxlength.converter.CatchMaxLengthConvert;
import com.backend.domain.catchmaxlength.entity.CatchMaxLength;
import com.backend.domain.catchmaxlength.repository.CatchMaxLengthRepository;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.converter.FishEncyclopediaConverter;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.fishencyclopedia.repository.FishEncyclopediaRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishEncyclopediaServiceImpl implements FishEncyclopediaService {

	private final FishEncyclopediaRepository fishEncyclopediaRepository;
	private final FishPointRepository fishPointRepository;
	private final CatchMaxLengthRepository catchMaxLengthRepository;
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

		// 물고기 최대 길이 값 조회
		Optional<CatchMaxLength> findEncyclopediaMaxLength = catchMaxLengthRepository
			.findByFishIdAndMemberId(
				requestDto.fishId(),
				memberId
			);

		// 비어있다면 객체 생성
		CatchMaxLength catchMaxLength = findEncyclopediaMaxLength
			.orElse(CatchMaxLengthConvert.fromCatchMaxLength(requestDto.fishId(), memberId));

 		// 최대 값 갱신 & 잡은 마리 수 설정
		catchMaxLength.setBestLength(requestDto.length());
		catchMaxLength.setCatchCount(requestDto.count());

		log.debug("잡은 물고기 데이터 갱신: {}", catchMaxLength);

		if (
			catchMaxLength.getBestLength().equals(requestDto.length()) ||
			catchMaxLength.getCatchCount().equals(requestDto.count())
		) {
			catchMaxLengthRepository.save(catchMaxLength);
		}

		FishEncyclopedia savedFishEncyclopedia = fishEncyclopediaRepository.createFishEncyclopedia(fishEncyclopedia);

		log.debug("물고기 도감 저장: {}", savedFishEncyclopedia);

		return savedFishEncyclopedia.getFishEncyclopediaId();
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<FishEncyclopediaResponse.Detail> getDetailList(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	) {
		ScrollResponse<FishEncyclopediaResponse.Detail> findDetailList = fishEncyclopediaRepository.findDetailByAllByMemberIdAndFishId(
			cursorRequestDto, fishId, memberId);

		log.debug("물고기 도감 상세 조회: {}", findDetailList);

		return findDetailList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FishEncyclopediaResponse.DetailPage> getDetailPageList(final Long memberId) {
		return fishEncyclopediaRepository.findDetailPageByAllByMemberId(memberId);
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
			throw new FishException(FishErrorCode.FISH_NOT_FOUND);
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
			throw new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND);
		}
	}
}
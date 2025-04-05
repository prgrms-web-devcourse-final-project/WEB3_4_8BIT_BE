package com.backend.domain.fish.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishServiceImpl implements FishService {

	private final FishRepository fishRepository;

	@Override
	public FishResponse.Detail getFishDetail(final Long fishId) {

		FishResponse.Detail getDetail = fishRepository.findDetailById(fishId)
			.orElseThrow(() -> new FishException(FishErrorCode.FISH_NOT_FOUND));

		log.debug("물고기 상세 조회: {}", getDetail);

		return getDetail;
	}

	@Override
	public List<FishResponse.Popular> getPopular(Integer size) {
		List<FishResponse.Popular> getPopularList = fishRepository.findPopular(size);

		log.debug("물고기 인기순 조회: {}", getPopularList);

		return getPopularList;
	}
}

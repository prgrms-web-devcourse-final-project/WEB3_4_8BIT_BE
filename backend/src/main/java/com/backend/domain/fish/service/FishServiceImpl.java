package com.backend.domain.fish.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishServiceImpl implements FishService {

	private final FishRepository fishRepository;

	@Override
	public FishResponse.Detail getFishDetail(final Long fishId) {

		return fishRepository.findDetailById(fishId)
			.orElseThrow(() -> new FishException(FishErrorCode.FISH_NOT_FOUND));
	}

	@Override
	public List<FishResponse.Popular> getPopular(Integer size) {
		return fishRepository.findPopular(size);
	}
}

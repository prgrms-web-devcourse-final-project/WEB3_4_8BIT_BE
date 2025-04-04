package com.backend.domain.fish.service;

import org.springframework.stereotype.Service;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FishServiceImpl implements FishService {

	private final FishRepository fishRepository;

	@Override
	public FishResponse.Detail getFishDetail(Long fishId) {

		return fishRepository.findById(fishId)
			.orElseThrow(() -> new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND));
	}
}

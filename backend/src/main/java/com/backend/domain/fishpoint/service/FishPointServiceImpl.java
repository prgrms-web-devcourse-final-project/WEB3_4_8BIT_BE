package com.backend.domain.fishpoint.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.repository.FishPointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishPointServiceImpl implements FishPointService {

	private final FishPointRepository fishPointRepository;

	@Override
	public List<FishPointResponse> getFishPointsByBounds(
		final double swLat,
		final double swLng,
		final double neLat,
		final double neLng
	) {
		return fishPointRepository.findByBounds(swLat, swLng, neLat, neLng);
	}
}

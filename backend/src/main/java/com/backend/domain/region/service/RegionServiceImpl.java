package com.backend.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.region.converter.RegionConverter;
import com.backend.domain.region.dto.response.RegionResponse;
import com.backend.domain.region.repository.RegionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

	private final RegionRepository regionRepository;

	@Override
	@Transactional(readOnly = true)
	public List<RegionResponse.Basic> getAllRegions() {
		return regionRepository.findAll().stream().map(RegionConverter::from).toList();
	}
}

package com.backend.domain.captain.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CaptainRepositoryImpl implements CaptainRepository {

	private final CaptainJpaRepository captainJpaRepository;
	private final CaptainQueryRepository captainQueryRepository;

	@Override
	public Captain save(final Captain captain) {
		return captainJpaRepository.save(captain);
	}

	@Override
	public Optional<Captain> findById(final Long captainId) {
		return captainJpaRepository.findById(captainId);
	}

	@Override
	public Optional<CaptainResponse.Detail> findDetailById(final Long captainId) {
		return captainQueryRepository.findDetailById(captainId);
	}
}

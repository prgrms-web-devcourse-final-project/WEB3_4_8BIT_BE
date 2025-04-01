package com.backend.domain.catchmaxlength.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CatchMaxLengthRepositoryImpl implements CatchMaxLengthRepository {

	private final CatchMaxLengthJpaRepository catchMaxLengthJpaRepository;

	@Override
	public CatchMaxLength save(
		final CatchMaxLength catchMaxLength
	) {
		return catchMaxLengthJpaRepository.save(catchMaxLength);
	}

	@Override
	public Optional<CatchMaxLength> findByFishIdAndMemberId(
		final Long fishId,
		final Long memberId
	) {
		return catchMaxLengthJpaRepository.findByFishIdAndMemberId(fishId, memberId);
	}
}

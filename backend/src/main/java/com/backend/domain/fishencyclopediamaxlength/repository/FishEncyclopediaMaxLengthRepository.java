package com.backend.domain.fishencyclopediamaxlength.repository;

import java.util.Optional;

import com.backend.domain.fishencyclopediamaxlength.entity.FishEncyclopediaMaxLength;

public interface FishEncyclopediaMaxLengthRepository {

	/**
	 * 물고기 도감 최대 길이 저장 메소드
	 *
	 * @param fishEncyclopediaMaxLength {@link FishEncyclopediaMaxLength}
	 * @return {@link FishEncyclopediaMaxLength}
	 * @implSpec FishEncyclopediaMaxLength 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	FishEncyclopediaMaxLength save(final FishEncyclopediaMaxLength fishEncyclopediaMaxLength);

	/**
	 * 물고기 도감 최대 길이 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @param memberId {@link Long}
	 * @return {@link Optional<FishEncyclopediaMaxLength>}
	 * @implSpec fishId, memberId 받아서 일치하는 데이터 조회 후 반환
	 * @author Kim Dong O
	 */
	Optional<FishEncyclopediaMaxLength> findByFishIdAndMemberId(final Long fishId, final Long memberId);
}

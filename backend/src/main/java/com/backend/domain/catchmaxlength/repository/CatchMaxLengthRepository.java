package com.backend.domain.catchmaxlength.repository;

import java.util.Optional;

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;

public interface CatchMaxLengthRepository {

	/**
	 * 물고기 도감 최대 길이 저장 메소드
	 *
	 * @param catchMaxLength {@link CatchMaxLength}
	 * @return {@link CatchMaxLength}
	 * @implSpec FishEncyclopediaMaxLength 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	CatchMaxLength save(final CatchMaxLength catchMaxLength);

	/**
	 * 물고기 도감 최대 길이 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @param memberId {@link Long}
	 * @return {@link Optional< CatchMaxLength >}
	 * @implSpec fishId, memberId 받아서 일치하는 데이터 조회 후 반환
	 * @author Kim Dong O
	 */
	Optional<CatchMaxLength> findByFishIdAndMemberId(final Long fishId, final Long memberId);
}

package com.backend.domain.fishencyclopedia.repository;

import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;

public interface FishEncyclopediaRepository {

	/**
	 * 물고기 도감 저장 메소드
	 *
	 * @param fishEncyclopedia {@link FishEncyclopedia}
	 * @return {@link FishEncyclopedia}
	 * @implSpec FishEncyclopedia 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	FishEncyclopedia save(final FishEncyclopedia fishEncyclopedia);
}

package com.backend.domain.fish.service;

import java.util.List;

import com.backend.domain.fish.dto.FishResponse;

public interface FishService {

	/**
	 * 물고기 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @return {@link FishResponse.Detail}
	 * @implSpec fishId 받아서 조회 후 Optional로 감싸서 반환
	 * @author Kim Dong O
	 */
	FishResponse.Detail getFishDetail(final Long fishId);

	/**
	 * 물고기 인기순 조회 메소드
	 *
	 * @param size {@link Integer}
	 * @return {@link List<FishResponse.Popular>}
	 * @implSpec limit 받아서 limit 개수만큼 조회 후 결과 반환
	 * @author Kim Dong O
	 */
	List<FishResponse.Popular> getPopular(final Integer size);

}

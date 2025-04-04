package com.backend.domain.fish.service;

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
}

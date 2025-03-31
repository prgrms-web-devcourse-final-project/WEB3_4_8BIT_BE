package com.backend.domain.fish.repository;

import com.backend.domain.fish.entity.Fish;

public interface FishRepository {
	/**
	 * 물고기 존재 여부 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @return {@link Boolean} 데이터가 있다면 true, 없으면 false
	 * @implSpec fishId로 데이터가 있는지 확인 후 결과 반한
	 * @author Kim Dong O
	 */
	boolean existsById(final Long fishId);

	/**
	 * 물고기 저장 메소드
	 *
	 * @param fish {@link Fish}
	 * @return {@link Fish}
	 * @implSpec FishPoint 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	Fish save(final Fish fish);
}

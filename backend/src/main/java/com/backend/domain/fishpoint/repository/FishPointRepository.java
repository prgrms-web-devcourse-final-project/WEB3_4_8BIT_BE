package com.backend.domain.fishpoint.repository;

import com.backend.domain.fishpoint.entity.FishPoint;

public interface FishPointRepository {

	/**
	 * 낚시 포인트 존재 여부 조회 메소드
	 *
	 * @param fishPointId {@link Long}
	 * @return {@link Boolean} 데이터가 있다면 true, 없으면 false
	 * @implSpec fishPointId로 데이터가 있는지 확인 후 결과 반한
	 * @author Kim Dong O
	 */
	boolean existsById(final Long fishPointId);

	/**
	 * 낚시 포인트 저장 메소드
	 *
	 * @param fishPoint {@link FishPoint}
	 * @return {@link FishPoint}
	 * @implSpec FishPoint 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	FishPoint save(final FishPoint fishPoint);
}

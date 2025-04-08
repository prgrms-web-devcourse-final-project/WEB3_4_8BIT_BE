package com.backend.domain.fishpoint.repository;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

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

	/**
	 * 지도에서 특정 바운드 영역 내에 포함된 낚시 포인트 목록을 조회
	 *
	 * @param swLat 남서쪽(South-West) 위도
	 * @param swLng 남서쪽(South-West) 경도
	 * @param neLat 북동쪽(North-East) 위도
	 * @param neLng 북동쪽(North-East) 경도
	 * @return 바운드 내에 존재하는 낚시 포인트 정보를 담은 DTO 리스트
	 */
	List<Response> findByBounds(final double swLat, final double swLng, final double neLat, final double neLng);

	/**
	 * 중심 좌표 기준으로 반경 내 낚시 포인트 조회
	 *
	 * @param lat 중심 위도
	 * @param lng 중심 경도
	 * @param radiusKm 반경 (킬로미터 단위)
	 * @return 반경 내 낚시 포인트 리스트
	 */
	List<ResponseWithDistance> findByDistanceWithin(final double lat, final double lng, final double radiusKm);

	/**
	 * 사용자의 현재 위치를 기준으로 가장 가까운 낚시 포인트 3개를 조회
	 *
	 * @param lat 사용자의 현재 위도
	 * @param lng 사용자의 현재 경도
	 * @return 거리 정보가 포함된 낚시 포인트 응답 리스트 (최대 3개)
	 */
	List<ResponseWithDistance> findNearestFishPoints(final double lat, final double lng);

	/**
	 * 지정한 지역(도 단위) ID에 해당하는 모든 낚시 포인트 정보를 조회
	 *
	 * @param regionId 조회할 지역의 ID
	 * @return 해당 지역에 속한 낚시 포인트 리스트
	 */
	List<Response> findByRegionId(final Long regionId);

	/**
	 * 지역명을 기준으로 낚시 포인트 전체 조회
	 *
	 * @param fishPointName 지역명 (부분 일치 검색)
	 * @return 낚시 포인트 정보 DTO 리스트
	 */
	List<Response> findByFishPointName(final String fishPointName);
}

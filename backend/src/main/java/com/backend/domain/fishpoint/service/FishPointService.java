package com.backend.domain.fishpoint.service;

import static com.backend.domain.fishpoint.dto.response.FishPointResponse.*;

import java.util.List;

public interface FishPointService {

	/**
	 * 주어진 지도 바운드 영역 내에 포함된 낚시 포인트들을 조회
	 *
	 * @param swLat 지도 바운드의 남서쪽(South-West) 위도
	 * @param swLng 지도 바운드의 남서쪽(South-West) 경도
	 * @param neLat 지도 바운드의 북동쪽(North-East) 위도
	 * @param neLng 지도 바운드의 북동쪽(North-East) 경도
	 * @return 조회된 낚시 포인트 응답 DTO 리스트
	 */
	List<Basic> getFishPointsByBounds(
		final double swLat,
		final double swLng,
		final double neLat,
		final double neLng
	);

	/**
	 * 사용자의 현재 위치를 기준으로 지정된 반경(km) 내의 낚시 포인트를 조회
	 *
	 * @param lat 위도 (latitude) - 사용자의 현재 위치 위도
	 * @param lng 경도 (longitude) - 사용자의 현재 위치 경도
	 * @param radiusKm 반경 거리 (단위: km) - 조회할 거리 범위
	 * @return 반경 내에 위치한 낚시 포인트 정보를 담은 DTO 리스트
	 */
	List<WithDistance> getNearbyFishPoints(final double lat, final double lng, final double radiusKm);

	/**
	 * 사용자의 현재 위치를 기준으로 가장 가까운 낚시 포인트 3개를 조회
	 *
	 * @param lat 사용자의 현재 위도
	 * @param lng 사용자의 현재 경도
	 * @return 거리 정보가 포함된 낚시 포인트 응답 리스트 (최대 3개)
	 */
	List<WithDistance> getNearestFishPoints(final double lat, final double lng);

	/**
	 * 주어진 지역 ID에 해당하는 낚시 포인트 목록을 조회
	 *
	 * @param regionId 조회할 지역의 ID
	 * @return 해당 지역에 속한 낚시 포인트 정보를 담은 Response 리스트
	 */
	List<Basic> getFishPointsByRegionId(final Long regionId);

	/**
	 * 낚시 포인트 이름을 기준으로 검색
	 *
	 * @param fishPointName 검색할 낚시 포인트 이름 (부분 일치)
	 * @return 검색된 낚시 포인트 목록 (isBan = false인 데이터만 반환)
	 */
	List<Basic> searchFishPoints(final String fishPointName);

	/**
	 * 동출 게시글 수를 기준으로 인기 있는 낚시 포인트 상위 3개를 조회합니다.
	 *
	 * @return 동출 게시글 수가 많은 순으로 정렬된 인기 낚시 포인트 3개의 리스트
	 */
	List<Popularity> getPopularityFishPoints();

	/**
	 * 주어진 낚시 포인트 ID에 해당하는 상세 정보 조회
	 *
	 * @param fishPointId 조회할 낚시 포인트의 ID
	 * @return 해당 낚시 포인트의 상세 정보를 담은 {@link Detail} 객체
	 */
	Detail getFishPointDetail(final Long fishPointId);
}

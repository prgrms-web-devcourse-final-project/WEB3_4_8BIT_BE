package com.backend.domain.fishpoint.service;

import java.util.List;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;

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
	List<FishPointResponse> getFishPointsByBounds(
		final double swLat,
		final double swLng,
		final double neLat,
		final double neLng
	);

	/**
	 * 낚시 포인트 이름을 기준으로 검색
	 *
	 * @param fishPointName 검색할 낚시 포인트 이름 (부분 일치)
	 * @return 검색된 낚시 포인트 목록 (isBan = false인 데이터만 반환)
	 */
	List<FishPointResponse> searchFishPoints(final String fishPointName);
}

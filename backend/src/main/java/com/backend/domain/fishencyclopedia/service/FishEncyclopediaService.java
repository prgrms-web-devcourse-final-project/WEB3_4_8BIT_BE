package com.backend.domain.fishencyclopedia.service;

import java.util.List;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface FishEncyclopediaService {

	/**
	 * 물고기 도감 상세 조회 메소드
	 *
	 * @param requestDto   {@link FishEncyclopediaRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link Long}
	 * @implSpec FishEncyclopediasRequest.Create 받아서 저장 후 ID 값 반환
	 * @author Kim Dong O
	 */
	Long createFishEncyclopedia(final FishEncyclopediaRequest.Create requestDto, final Long memberId);

	/**
	 * 물고기 도감 저장 메소드
	 *
	 * @param pageRequestDto   {@link GlobalRequest.PageRequest}
	 * @param fishId {@link Long}
	 * @param memberId {@link Long}
	 * @return {@link Long}
	 * @implSpec requestDto.PageRequest, fishId, memberId 받아서 조회 후 결과 값 반환
	 * @author Kim Dong O
	 */
	ScrollResponse<FishEncyclopediaResponse.Detail> getDetailList(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	);

		/**
	 * 물고기 도감 전체 조회 메소드
	 *
	 * @param memberId {@link Long}
	 * @return {@link Long}
	 * @implSpec requestDto.PageRequest, fishId, memberId 받아서 조회 후 결과 값 반환
	 * @author Kim Dong O
	 */
	List<FishEncyclopediaResponse.DetailPage> getDetailPageList(
		final Long memberId
	);
}

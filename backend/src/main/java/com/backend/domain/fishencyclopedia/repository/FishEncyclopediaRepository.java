package com.backend.domain.fishencyclopedia.repository;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface FishEncyclopediaRepository {

	/**
	 * 물고기 도감 저장 메소드
	 *
	 * @param fishEncyclopedia {@link FishEncyclopedia}
	 * @return {@link FishEncyclopedia}
	 * @implSpec FishEncyclopedia 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	FishEncyclopedia createFishEncyclopedia(final FishEncyclopedia fishEncyclopedia);

	/**
	 * 물고기 도감 상세 조회 메소드
	 *
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @param fishId           {@link Long}
	 * @param memberId           {@link Long}
	 * @return {@link ScrollResponse}
	 * @implSpec FishId가 일치하는 데이터 동적 조회 후 결과 반환
	 * Sort - length, sort, createdAt(default)
	 * Order - ASC, DESC(default)
	 * @author Kim Dong O
	 */
	ScrollResponse<FishEncyclopediaResponse.Detail> findDetailByAllByMemberIdAndFishId(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	);

	/**
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @param fishId           {@link Long}
	 * @param memberId         {@link Long}
	 * @return {@link ScrollResponse}
	 * @implSpec FishId가 일치하는 데이터 동적 조회 후 결과 반환
	 * Sort - length, sort, createdAt(default)
	 * Order - ASC, DESC(default)
	 * @author Kim Dong O
	 */
	ScrollResponse<FishEncyclopediaResponse.DetailPage> findDetailPageByAllByMemberIdAndFishId(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	);
}

package com.backend.domain.fishencyclopedia.repository;

import java.util.List;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.querydsl.core.Tuple;

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
	 * @param memberId         {@link Long}
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
	 * @param memberId {@link Long}
	 * @return {@link ScrollResponse}
	 * @implSpec FishId가 일치하는 데이터 동적 조회 후 결과 반환
	 * 관리자가 직접 추가하는 데이터이기 때문에 일단은 findAll 형태로 구현
	 * @author Kim Dong O
	 */
	List<FishEncyclopediaResponse.DetailPage> findDetailPageByAllByMemberId(
		final Long memberId
	);

	/**
	 * 현재 시간 1시간 전부터 현재 시간까지 도감에 추가된 물고기 count 조회 메소드
	 * <p>첫 번째: fishId, 두 번째: count</p>
	 *
	 * @return {@link List<Tuple>}
	 * @implSpec 현재 시간 1시간 전부터 현재 시간까지 도감에 추가된 물고기 count를
	 * 조회하여 {@link List<Tuple>}로 반환
	 */
	List<Tuple> findHourlyFishCountSummary();

	/**
	 * 현재 시각 기준, 1시간 전부터 지금까지 낚시 포인트별로 도감에 추가된 물고기 수를 조회합니다.
	 * <p>첫 번째: fishPointId, 두 번째: fishId, 세 번째: count</p>
	 *
	 * @return {@link List<Tuple>}
	 * @implSpec 1시간 내 도감에 추가된 물고기 데이터를 낚시 포인트 기준으로 그룹핑하여 조회
	 */
	List<Tuple> findFishPointHourlyFishCountSummary();


	/**
	 * 특정 회원이 어류도감에 등록한 어종(Fish)의 개수를 조회
	 *
	 * @param memberId {@link Long}
	 * @return 등록된 어종의 개수
	 * @implSpec 중복되지 않는 fishId 기준으로 회원이 도감에 등록한 어종의 개수를 반환
	 */
	Long countDistinctFishIdByMemberId(final Long memberId);
}

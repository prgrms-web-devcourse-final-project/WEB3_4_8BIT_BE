package com.backend.domain.shipfishingpost.repository;

import java.util.Optional;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface ShipFishingPostRepository {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param shipFishingPost {@link ShipFishingPost}
	 * @return {@link ShipFishingPost}
	 * @implSpec 선상 낚시 게시글 저장 메서드 입니다.
	 */
	ShipFishingPost save(final ShipFishingPost shipFishingPost);

	/**
	 * 선상 낚시 게시글 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link Optional<ShipFishingPost>}
	 * @implSpec 선상 낚시 게시글 Entity 를 반환한다.
	 */
	Optional<ShipFishingPost> findById(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드 (게시글, 멤버, 선박 정보 포함)
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.DetailAll}
	 * @implSpec 선상 낚시 게시글 ID를 받아서 조회 후 ShipFishingPostResponse.DetailAll 을 반환한다.
	 */
	Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 목록 조회 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Search}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ShipFishingPostResponse.DetailScroll>}
	 * @implSpec 선상 낚시 게시글 검색 조건을 입력받아 필터링된 게시글 값들을 반환한다.
	 */
	ScrollResponse<ShipFishingPostResponse.DetailScroll> findDetailScrollBySearch(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 선상 낚시 게시글 삭제 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @implSpec 선상 낚시 게시글을 삭제합니다.
	 */
	void deleteById(final Long shipFishingPostId);

	/**
	 * 선박 ID 기준 선상 낚시 게시글 존재 여부 확인하는 메소드
	 *
	 * @param shipId {@link Long}
	 * @return {@link Boolean}
	 * @implSpec 선박 ID 기준 선상 낚시 게시글 존재 여부 결과 값 반환
	 */
	boolean existsByShipId(final Long shipId);
}
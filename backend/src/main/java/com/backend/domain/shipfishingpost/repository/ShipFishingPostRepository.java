package com.backend.domain.shipfishingpost.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.global.util.pageutil.Page;

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
	 * 선상 낚시 게시글 상세 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.Detail}
	 * @implSpec 선상 낚시 게시글 ID를 받아서 조회 후 ShipFishingPostResponse.Detail 을 반환한다.
	 */
	Optional<ShipFishingPostResponse.Detail> findDetailById(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드 (게시글, 멤버, 선박 정보 포함)
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.DetailAll}
	 * @implSpec 선상 낚시 게시글 ID를 받아서 조회 후 ShipFishingPostResponse.DetailAll 을 반환한다.
	 */
	Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 조회 메서드
	 *
	 * @param search {@link ShipFishingPostRequest.Search}
	 * @param pageable {@link Pageable}
	 * @return {@link ShipFishingPostResponse.DetailPage}
	 * @implSpec 여러 조건들과 검색 키워드를 통해 ShipFishingPostResponse.detailPage 을 페이징 하여 반환한다.
	 */
	Slice<ShipFishingPostResponse.DetailPage> findAllBySearchAndCondition(final ShipFishingPostRequest.Search search,
		final Pageable pageable);
}
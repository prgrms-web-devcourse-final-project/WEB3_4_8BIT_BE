package com.backend.domain.shipfishingpost.service;

import org.springframework.data.domain.Pageable;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.global.util.pageutil.Page;

public interface ShipFishingPostService {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @return {@link Long ShipFishPostId} Long: 게시글 id
	 * @implSpec 선상 낚시 게시글 정보를 파라미터로 받고 저장한다.
	 * @author swjoon
	 */
	Long saveShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.Detail}
	 * @implSpec 선상 낚시 게시글 번호를 파라미터로 받고 조회한다.
	 * @author swjoon
	 */
	ShipFishingPostResponse.Detail getShipFishingPost(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드 (게시글, 멤버, 선박 정보 포함)
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.DetailAll}
	 * @implSpec 선상 낚시 게시글 번호를 파라미터로 받고 조회한다.
	 * @author swjoon
	 */
	ShipFishingPostResponse.DetailAll getShipFishingPostAll(final Long shipFishingPostId);

	Page<ShipFishingPostResponse.DetailPage> getShipFishingPostPage(final ShipFishingPostRequest.Search requestDto,
		final Pageable pageable);

}
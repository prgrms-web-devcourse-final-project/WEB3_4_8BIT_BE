package com.backend.domain.shipfishingpost.service;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;

public interface ShipFishingPostService {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @return {@link Long ShipFishPostId} Long: 게시글 id
	 * @implSpec 선상 낚시 게시글 정보를 파라미터로 받고 저장한다.
	 * @author swjoon
	 */
	Long save(ShipFishingPostRequest.Create requestDto);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.Detail}
	 * @implSpec 선상 낚시 게시글 번호를 파라미터로 받고 조회한다.
	 * @author swjoon
	 */
	ShipFishingPostResponse.Detail getShipFishingPost(Long shipFishingPostId);

}
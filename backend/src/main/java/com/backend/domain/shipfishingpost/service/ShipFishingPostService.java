package com.backend.domain.shipfishingpost.service;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;

public interface ShipFishingPostService {

	/**
	 * 선상 낚시 게시글 저장 메소드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @return {@link Long ShipFishPostId} Long: 게시글 id
	 * @implSpec 선상 낚시 게시글 정보를 파라미터로 받고 저장한다.
	 * @author swjoon
	 */
	Long save(ShipFishingPostRequest.Create requestDto);

}
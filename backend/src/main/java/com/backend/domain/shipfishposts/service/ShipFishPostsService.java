package com.backend.domain.shipfishposts.service;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;

public interface ShipFishPostsService {

	/**
	 * 게시물 저장 메소드
	 *
	 * @param requestDto {@link ShipFishPostsRequest.Create}
	 * @return {@link Long ShipFishPostId} Long: 게시글 id
	 * @implSpec 게시물 정보를 파라미터로 받고 저장한다.
	 * @author swjoon
	 */
	Long save(ShipFishPostsRequest.Create requestDto);

}

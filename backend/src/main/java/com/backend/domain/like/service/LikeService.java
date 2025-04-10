package com.backend.domain.like.service;

import com.backend.domain.like.dto.request.LikeRequest;

public interface LikeService {

	/**
	 * 좋아요 토글 메서드
	 *
	 * @param memberId   멤버 ID
	 * @param requestDto 좋아요 요청 DTO
	 * @implSpec 이미 좋아요한 경우 삭제, 좋아요하지 않은 경우 추가
	 */
	void toggleLike(final Long memberId, final LikeRequest requestDto);
}

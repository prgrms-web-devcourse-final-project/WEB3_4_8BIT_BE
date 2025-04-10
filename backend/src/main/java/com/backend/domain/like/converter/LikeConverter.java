package com.backend.domain.like.converter;

import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.entity.Like;

public class LikeConverter {

	/**
	 * 좋아요 요청 DTO를 Like 엔티티로 변환
	 *
	 * @param requestDto {@link LikeRequest}
	 * @return {@link Like}
	 */
	public static Like fromMemberAndLikeRequestCreate(
		final Long memberId,
		final LikeRequest requestDto
	) {
		return Like.builder()
			.memberId(memberId)
			.targetType(requestDto.targetType())
			.targetId(requestDto.targetId())
			.build();
	}
}

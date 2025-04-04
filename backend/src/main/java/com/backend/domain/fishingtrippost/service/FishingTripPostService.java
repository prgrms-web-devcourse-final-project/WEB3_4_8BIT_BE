package com.backend.domain.fishingtrippost.service;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;

public interface FishingTripPostService {

	/**
	 * 동출 게시글 저장 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 작성에 필요한 정보를 받아 게시글 작성
	 */
	Long createFishingTripPost(
		final Long memberId,
		final FishingTripPostRequest.Form requestDto
	);

	/**
	 * 동출 게시글 수정 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 수정에 필요한 정보를 받아 게시글 수정
	 */
	Long updateFishingTripPost(
		final Long memberId,
		final Long fishingTripPostId,
		final FishingTripPostRequest.Form requestDto
	);
}

package com.backend.domain.like.service;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface LikeService {

	/**
	 * 좋아요 토글 메서드
	 *
	 * @param memberId   멤버 ID
	 * @param requestDto 좋아요 요청 DTO
	 * @implSpec 이미 좋아요한 경우 삭제, 좋아요하지 않은 경우 추가
	 */
	void toggleLike(final Long memberId, final LikeRequest requestDto);

	/**
	 * 로그인한 사용자가 좋아요한 낚시 동행 게시글 목록을 커서 기반으로 조회합니다.
	 *
	 * <p>정렬 기준은 좋아요를 누른 시각(like.createdAt)과 likeId이며,
	 * 각 게시글은 {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.DetailPage}로 변환됩니다.</p>
	 *
	 * <p>대표 이미지 URL은 게시글의 fileIdList 중 첫 번째 ID를 기준으로 조회되며, 존재하지 않을 경우 null입니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보
	 * @param memberId         현재 로그인한 사용자 ID
	 * @return 좋아요한 게시글 목록에 대한 커서 기반 {@link com.backend.global.dto.response.ScrollResponse}
	 */
	ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse> getLikedFishingTripPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

	/**
	 * 로그인한 사용자가 좋아요한 선상 낚시 게시글 목록을 커서 기반으로 조회합니다.
	 *
	 * <p>정렬 기준은 좋아요를 누른 시각(like.createdAt)과 likeId이며,
	 * 각 게시글은 {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.DetailPage}로 변환됩니다.</p>
	 *
	 * <p>대표 이미지 URL은 게시글의 fileIdList 중 첫 번째 ID를 기준으로 조회되며, 존재하지 않을 경우 null입니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보
	 * @param memberId         현재 로그인한 사용자 ID
	 * @return 좋아요한 게시글 목록에 대한 커서 기반 {@link com.backend.global.dto.response.ScrollResponse}
	 */
	ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse> getLikedShipFishingPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

	/**
	 * 로그인한 사용자가 좋아요한 게시글 개수를 조회합니다.
	 *
	 * @param memberId   로그인한 유저 Id
	 * @param targetType 게시글 종류
	 * @return 사용자가 좋아요한 선상 낚시 게시글 개수
	 */
	Long getCountLikedShipFishingPosts(final Long memberId, final LikeTargetType targetType);
}

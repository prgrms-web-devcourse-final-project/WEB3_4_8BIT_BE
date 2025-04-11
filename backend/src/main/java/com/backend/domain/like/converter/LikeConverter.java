package com.backend.domain.like.converter;

import java.util.List;
import java.util.function.Function;

import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
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

	/**
	 * 좋아요한 동출 게시글 QueryDto를 응답 DTO로 변환합니다.
	 *
	 * <p>fileIdList에서 첫 번째 ID를 기반으로 대표 이미지 URL을 조회하여 포함시킵니다.</p>
	 *
	 * @param dto             {@link LikeResponse.FishingTripPostLikedQueryDto}
	 * @param fileUrlResolver {@link Function} - 파일 ID로 URL을 조회하는 함수
	 * @return {@link LikeResponse.FishingTripPostLikedDetailResponse}
	 */
	public static LikeResponse.FishingTripPostLikedDetailResponse toFishingTripPostDetailPage(
		final LikeResponse.FishingTripPostLikedQueryDto dto,
		final Function<Long, String> fileUrlResolver
	) {
		Long firstFileId = (dto.fileIdList() != null && !dto.fileIdList().isEmpty())
			? dto.fileIdList().get(0)
			: null;

		String imageUrl = (firstFileId != null) ? fileUrlResolver.apply(firstFileId) : null;

		return LikeResponse.FishingTripPostLikedDetailResponse.builder()
			.fishingTripPostId(dto.fishingTripPostId())
			.regionId(dto.regionId())
			.regionType(dto.regionType())
			.subject(dto.subject())
			.content(dto.content())
			.fishingDate(dto.fishingDate())
			.recruitmentCount(dto.recruitmentCount())
			.postStatus(dto.postStatus())
			.imageUrl(imageUrl)
			.commentCount(dto.commentCount())
			.likeCount(dto.likeCount())
			.build();
	}

	/**
	 * 좋아요한 선상낚시 게시글 QueryDto를 응답 DTO로 변환합니다.
	 *
	 * @param dto             {@link LikeResponse.ShipFishingPostLikedQueryDto}
	 * @param fileUrlResolver {@link Function} - 파일 ID로 URL을 조회하는 함수
	 * @return {@link LikeResponse.ShipFishingPostLikedDetailResponse}
	 */
	public static LikeResponse.ShipFishingPostLikedDetailResponse toShipFishingPostDetailPage(
		final LikeResponse.ShipFishingPostLikedQueryDto dto,
		final List<String> fishNameList,
		final Function<Long, String> fileUrlResolver
	) {
		Long firstFileId = (dto.fileIdList() != null && !dto.fileIdList().isEmpty())
			? dto.fileIdList().get(0)
			: null;

		String imageUrl = (firstFileId != null) ? fileUrlResolver.apply(firstFileId) : null;

		return LikeResponse.ShipFishingPostLikedDetailResponse.builder()
			.shipFishingPostId(dto.shipFishingPostId())
			.subject(dto.subject())
			.location(dto.location())
			.price(dto.price())
			.fileUrl(imageUrl)
			.fishNameList(fishNameList)
			.reviewEverRate(dto.reviewEverRate())
			.likeCount(dto.likeCount())
			.build();
	}
}

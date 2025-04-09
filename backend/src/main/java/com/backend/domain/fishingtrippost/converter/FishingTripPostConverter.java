package com.backend.domain.fishingtrippost.converter;

import java.util.function.Function;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;

public class FishingTripPostConverter {

	/**
	 * 로그인한 멤버가 모집 게시글 작성 Dto를 Entity로 변환 메서드
	 *
	 * @param memberId   {@link Long}
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link FishingTripPost}
	 */

	public static FishingTripPost fromCreate(
		final Long memberId,
		final FishingTripPostRequest.Form requestDto
	) {
		return FishingTripPost.builder()
			.subject(requestDto.subject())
			.content(requestDto.content())
			.recruitmentCount(requestDto.recruitmentCount())
			.currentCount(0)
			.isShipFish(requestDto.isShipFish())
			.fishingDate(requestDto.fishingDate())
			.fishingPointId(requestDto.fishingPointId())
			.memberId(memberId)
			.fileIdList(requestDto.fileIdList())
			.regionId(requestDto.regionId())
			.postStatus(PostStatus.RECRUITING)
			.build();
	}

	/**
	 * 게시글 상세 페이지 목록 응답 객체로 변환하는 정적 팩토리 메서드입니다.
	 *
	 * <p>QueryDSL에서 조회한 {@link FishingTripPostResponse.DetailPageQueryDto} 데이터를 기반으로,
	 * 대표 이미지 ID가 존재할 경우 해당 ID를 통해 URL을 조회한 뒤 최종 응답 DTO인 {@link FishingTripPostResponse.DetailPage}를 생성합니다.</p>
	 *
	 * @param detailPageQueryDto {@link FishingTripPostResponse.DetailPageQueryDto} - 게시글의 기본 정보와 이미지 ID 목록을 포함하는 중간 DTO
	 * @param fileUrlResolver {@link Function} - 파일 ID를 통해 이미지 URL을 매핑하는 함수 (예: fileStorageService::getUrlById)
	 * @return {@link FishingTripPostResponse.DetailPage} - 클라이언트에 전달될 게시글 응답 객체
	 *
	 * <p>주의: fileIdList가 null 또는 비어있는 경우 imageUrl은 null로 설정됩니다.</p>
	 */
	public static FishingTripPostResponse.DetailPage toDetailPage(
		final FishingTripPostResponse.DetailPageQueryDto detailPageQueryDto,
		final Function<Long, String> fileUrlResolver
	) {
		Long firstFileId = (detailPageQueryDto.fileIdList() != null && !detailPageQueryDto.fileIdList().isEmpty())
			? detailPageQueryDto.fileIdList().get(0)
			: null;

		String imageUrl = (firstFileId != null) ? fileUrlResolver.apply(firstFileId) : null;

		return FishingTripPostResponse.DetailPage.builder()
			.fishingTripPostId(detailPageQueryDto.fishingTripPostId())
			.regionType(detailPageQueryDto.regionType())
			.subject(detailPageQueryDto.subject())
			.content(detailPageQueryDto.content())
			.fishingDate(detailPageQueryDto.fishingDate())
			.createdAt(detailPageQueryDto.createdAt())
			.recruitmentCount(detailPageQueryDto.recruitmentCount())
			.postStatus(detailPageQueryDto.postStatus())
			.imageUrl(imageUrl)
			.build();
	}
}

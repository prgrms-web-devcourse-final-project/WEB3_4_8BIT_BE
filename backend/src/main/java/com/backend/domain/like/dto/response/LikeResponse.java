package com.backend.domain.like.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.region.entity.RegionType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class LikeResponse {

	/**
	 * <pre>
	 * {@code
	 * {
	 * "likeId": 1,
	 * "targetType": "SHIP_FISHING_POST",
	 * "targetId": 100
	 * }
	 * }
	 * </pre>
	 *
	 * @param likeId     좋아요 ID
	 * @param memberId   좋아요 누른 멤버 ID
	 * @param targetType 좋아요 대상 타입 (SHIP_FISHING_POST, FISHING_TRIP_POST)
	 * @param targetId   좋아요 대상 ID
	 */
	@Builder
	public record Detail(
		Long likeId,
		Long memberId,
		LikeTargetType targetType,
		Long targetId
	) {
		@QueryProjection
		public Detail {
		}
	}

	/**
	 * {@link FishingTripPostLikedDetailResponse} 응답 생성을 위한 QueryDSL 중간 DTO입니다.
	 *
	 * <p>쿼리에서 필요한 게시글의 원본 필드 데이터를 추출하며, 이후 서비스 레이어에서
	 * 대표 이미지 URL을 포함한 최종 응답 객체인 {@link FishingTripPostLikedDetailResponse}로 변환됩니다.</p>
	 *
	 * <p>fileIdList를 통해 게시글에 첨부된 이미지들의 ID 목록을 제공하며,
	 * 이를 기반으로 대표 이미지 URL을 매핑합니다.</p>
	 *
	 * <p>정렬 기준은 좋아요 누른 시점인 likedAt입니다.</p>
	 *
	 * @param fishingTripPostId 게시글 ID
	 * @param regionId          지역 ID
	 * @param regionType        지역 enum
	 * @param subject           게시글 제목
	 * @param content           게시글 요약 내용
	 * @param fishingDate       출조 예정일
	 * @param likedAt           좋아요 누른 시각
	 * @param recruitmentCount  모집 정원
	 * @param postStatus        게시글 상태
	 * @param fileIdList        이미지 파일 ID 리스트
	 * @param commentCount      댓글 개수
	 * @param likeCount         좋아요 수
	 */
	@Builder
	public record FishingTripPostLikedQueryDto(
		Long fishingTripPostId,
		Long regionId,
		RegionType regionType,
		String subject,
		String content,
		ZonedDateTime fishingDate,
		ZonedDateTime likedAt,
		Integer recruitmentCount,
		PostStatus postStatus,
		List<Long> fileIdList,
		Long commentCount,
		Long likeCount
	) {
		@QueryProjection
		public FishingTripPostLikedQueryDto {
		}
	}

	/**
	 * 좋아요 기반 낚시 동행 게시글의 요약 정보를 클라이언트에 전달하기 위한 최종 응답 DTO입니다.
	 *
	 * <p>스크롤 페이징 목록, 정렬, 필터링 등에서 사용되며, 게시글의 핵심 정보와 대표 이미지 URL을 제공합니다.</p>
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "fishingTripPostId": 12,
	 *   "regionId": 3,
	 *   "regionType": "JEOLLANAM_DO",
	 *   "subject": "여수 갈치 낚시 모집",
	 *   "content": "갈치 좋아하는 분 모여요~",
	 *   "fishingDate": "2025-05-10T06:00:00+09:00",
	 *   "recruitmentCount": 4,
	 *   "postStatus": "RECRUITING",
	 *   "imageUrl": "https://cdn.example.com/대표이미지.jpg"
	 *   "commentCount": 10
	 *   "likeCount": 10
	 * }
	 * }</pre>
	 *
	 * @param fishingTripPostId 게시글 ID
	 * @param regionId          지역 ID
	 * @param regionType        지역 구분 enum
	 * @param subject           게시글 제목
	 * @param content           게시글 내용 요약
	 * @param fishingDate       출조 예정일
	 * @param recruitmentCount  모집 정원
	 * @param postStatus        게시글 상태
	 * @param imageUrl          대표 이미지 URL
	 * @param commentCount      댓글 개수
	 * @param likeCount         좋아요 수
	 */
	@Builder
	public record FishingTripPostLikedDetailResponse(
		Long fishingTripPostId,
		Long regionId,
		RegionType regionType,
		String subject,
		String content,
		ZonedDateTime fishingDate,
		Integer recruitmentCount,
		PostStatus postStatus,
		String imageUrl,
		Long commentCount,
		Long likeCount
	) {
	}

	/**
	 * 선상낚시 좋아요 게시글 DTO (QueryDSL 전용)
	 *
	 * @param shipFishingPostId 게시글 ID
	 * @param subject           제목
	 * @param location          지역명
	 * @param price             1인 기준 금액
	 * @param fileIdList        이미지 파일 ID 리스트
	 * @param fishIdList        물고기 ID 리스트
	 * @param reviewEverRate    평점
	 * @param likedAt           좋아요 누른 시각
	 * @param likeCount         좋아요 수
	 * @param reviewCount       리뷰 수
	 */
	@Builder
	public record ShipFishingPostLikedQueryDto(
		Long shipFishingPostId,
		String subject,
		String location,
		Long price,
		List<Long> fileIdList,
		List<Long> fishIdList,
		Double reviewEverRate,
		ZonedDateTime likedAt,
		Long likeCount,
		Long reviewCount
		) {
		@QueryProjection
		public ShipFishingPostLikedQueryDto {
		}
	}

	/**
	 * 선상낚시 좋아요 게시글 최종 응답 DTO
	 * <p>
	 * 예시:
	 * {@code
	 * {
	 * "shipFishingPostId": 1,
	 * "subject": "즐거운 선상낚시",
	 * "location": "부산 기장군",
	 * "price": 80000,
	 * "fileUrl": "https://cdn.example.com/image.jpg",
	 * "fishNameList": ["참돔", "문어"],
	 * "reviewEverRate": 4.8,
	 * "reviewCount": 124,
	 * "likeCount": 10
	 * }
	 * }
	 *
	 * @param shipFishingPostId 게시글 ID
	 * @param subject           제목
	 * @param location          지역
	 * @param price             금액
	 * @param fileUrl           대표 이미지 URL
	 * @param fishNameList      물고기명 List
	 * @param reviewEverRate    평점
	 * @param reviewCount       리뷰 수
	 * @param likeCount         좋아요 수
	 */
	@Builder
	public record ShipFishingPostLikedDetailResponse(
		Long shipFishingPostId,
		String subject,
		String location,
		Long price,
		String fileUrl,
		List<String> fishNameList,
		Double reviewEverRate,
		Long reviewCount,
		Long likeCount
	) {
	}
}

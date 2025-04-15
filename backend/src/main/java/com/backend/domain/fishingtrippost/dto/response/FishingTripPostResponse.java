package com.backend.domain.fishingtrippost.dto.response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.region.entity.RegionType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class FishingTripPostResponse {

	/**
	 * 낚시 동행 게시글의 상세 정보를 담는 응답 DTO입니다.
	 *
	 * <p>게시글의 기본 정보, 작성자, 위치 정보, 첨부 이미지 URL 목록, 모집 상태,
	 * 좋아요 수 및 사용자의 좋아요 여부를 포함합니다.</p>
	 *
	 * <p>이 응답은 게시글 상세 조회 API에서 사용되며,
	 * {@link DetailQueryDto}, 이미지 URL 리스트, 좋아요 정보로 구성됩니다.</p>
	 *
	 * <p>예시 JSON 구조:</p>
	 * <pre>{@code
	 * {
	 *   "fishingTripPostId": 1,
	 *   "nickname": "루피",
	 *   "subject": "해적왕과 함께하는 낚시",
	 *   "content": "고무고무 낚시왕!",
	 *   "currentCount": 2,
	 *   "recruitmentCount": 4,
	 *   "createDate": "2025-04-03T12:30:00+09:00",
	 *   "fishingDate": "2025-04-10T06:00:00+09:00",
	 *   "fishPointDetailName": "동해 낚시 명소",
	 *   "fishPointName": "동해 포인트",
	 *   "longitude": 128.12345,
	 *   "latitude": 37.12345,
	 *   "fileUrlList": [ "https://cdn.example.com/image1.jpg", "https://cdn.example.com/image2.jpg" ],
	 *   "postStatus": "RECRUITING",
	 *   "likeCount": 12,
	 *   "isLiked": true
	 * }
	 * }</pre>
	 */

	@Builder
	public record Detail(
		Long fishingTripPostId,
		String nickname,
		String subject,
		String content,
		Integer currentCount,
		Integer recruitmentCount,
		ZonedDateTime createDate,
		ZonedDateTime fishingDate,
		String fishPointDetailName,
		String fishPointName,
		Double longitude,
		Double latitude,
		Map<Long, String> fileUrlList,
		PostStatus postStatus,
		Long likeCount,
		boolean isLiked,
		boolean isPostOwner
	) {
	}

	public record DetailQueryDto(
		Long fishingTripPostId,
		String name,
		String subject,
		String content,
		Integer currentCount,
		Integer recruitmentCount,
		ZonedDateTime createDate,
		ZonedDateTime fishingDate,
		String fishPointDetailName,
		String fishPointName,
		Double longitude,
		Double latitude,
		List<Long> fileIdList,
		PostStatus postStatus,
		Long likeCount
	) {
		@QueryProjection
		public DetailQueryDto {

		}
	}

	/**
	 * 낚시 동행 게시글의 요약 정보를 클라이언트에 전달하기 위한 응답 DTO입니다.
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
	 *   "createdAt": "2025-04-01T11:45:00+09:00",
	 *   "recruitmentCount": 4,
	 *   "postStatus": "RECRUITING",
	 *   "imageUrl": "https://cdn.example.com/대표이미지.jpg",
	 *   "commentCount": 10,
	 * 	 "likeCount": 10,
	 * 	 "popularity": 20,
	 * 	 "isLiked": true
	 * }
	 * }</pre>
	 *
	 * @param fishingTripPostId 게시글 ID
	 * @param regionId          지역 ID
	 * @param regionType        지역 구분 enum
	 * @param subject           게시글 제목
	 * @param content           게시글 내용 요약
	 * @param fishingDate       출조 예정일
	 * @param createdAt         게시글 생성일
	 * @param recruitmentCount  모집 정원
	 * @param postStatus        게시글 상태
	 * @param imageUrl          대표 이미지 URL
	 * @param commentCount      댓글수
	 * @param likeCount         좋아요수
	 * @param popularityScore   인기 점수
	 * @param isLiked           유저의 좋아요 여부
	 */
	@Builder
	public record DetailPage(
		Long fishingTripPostId,
		Long regionId,
		RegionType regionType,
		String subject,
		String content,
		ZonedDateTime fishingDate,
		ZonedDateTime createdAt,
		Integer recruitmentCount,
		PostStatus postStatus,
		String imageUrl,
		Long commentCount,
		Long likeCount,
		Long popularityScore,
		boolean isLiked
	) {
		@QueryProjection
		public DetailPage {

		}
	}

	/**
	 * {@link DetailPage} 응답 생성을 위한 QueryDSL 중간 DTO입니다.
	 *
	 * <p>쿼리에서 필요한 게시글의 원본 필드 데이터를 추출하며, 이후 서비스 레이어에서
	 * 대표 이미지 URL을 포함한 최종 응답 객체인 {@link DetailPage}로 변환됩니다.</p>
	 *
	 * <p>fileIdList를 통해 게시글에 첨부된 이미지들의 ID 목록을 제공하며,
	 * 이를 기반으로 대표 이미지 URL을 매핑합니다.</p>
	 */
	public record DetailPageQueryDto(
		Long fishingTripPostId,
		Long regionId,
		RegionType regionType,
		String subject,
		String content,
		ZonedDateTime fishingDate,
		ZonedDateTime createdAt,
		Integer recruitmentCount,
		PostStatus postStatus,
		List<Long> fileIdList,
		Long commentCount,
		Long likeCount
	) {
		@QueryProjection
		public DetailPageQueryDto {
		}
	}

	/**
	 * 낚시 동행 게시글 참여 상세 정보를 담는 응답 DTO입니다.
	 *
	 * <p>게시글의 참여 현황, 로그인 유저의 신청/작성자 여부, 작성자 정보 및 참여자 목록을 포함합니다.</p>
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "fishingTripPostId": 12,
	 *   "recruitmentCount": 6,
	 *   "currentCount": 3,
	 *   "postStatus": "RECRUITING",
	 *   "isApplicant": true,
	 *   "postOwnerId": 5,
	 *   "ownerNickname": "김동현",
	 *   "ownerProfileImageUrl": "https://cdn.example.com/profile.jpg",
	 *   "isCurrentUserOwner": false,
	 *   "participants": [
	 *     {
	 *       "memberId": 1,
	 *       "nickname": "강동현",
	 *       "profileImageUrl": "https://cdn.example.com/user1.jpg"
	 *     },
	 *     ...
	 *   ]
	 * }
	 * }</pre>
	 *
	 * @param fishingTripPostId    게시글 ID
	 * @param recruitmentCount     총 모집 인원
	 * @param currentCount         현재 승인된 참여자 수
	 * @param postStatus           게시글 상태 (RECRUITING, COMPLETED 등)
	 * @param isApplicant          현재 로그인한 사용자가 참여자인지 여부
	 * @param postOwnerId          게시글 작성자 ID
	 * @param ownerNickname        작성자 닉네임
	 * @param ownerProfileImageUrl 작성자 프로필 이미지 URL
	 * @param isCurrentUserOwner   현재 로그인 유저가 작성자인지 여부
	 * @param participants         승인된 참여자 리스트
	 */

	@Builder
	public record FishingTripPostParticipationDetail(
		Long fishingTripPostId,
		Integer recruitmentCount,
		Integer currentCount,
		PostStatus postStatus,
		boolean isApplicant,
		boolean isCurrentUserOwner,
		Long postOwnerId,
		String ownerNickname,
		String ownerProfileImageUrl,
		List<ParticipantDetail> participants
	) {
		@QueryProjection
		public FishingTripPostParticipationDetail {

		}
	}

	public record ParticipantDetailDto(
		Long fishingTripPostId,
		Integer recruitmentCount,
		Integer currentCount,
		PostStatus postStatus,
		boolean isApplicant,
		boolean isCurrentUserOwner,
		Long postOwnerId,
		String ownerNickname,
		String ownerProfileImageUrl
	) {
		@QueryProjection
		public ParticipantDetailDto {
		}
	}

	/**
	 * 게시글 참여자 정보 DTO입니다.
	 *
	 * <p>게시글에 승인된 참여 멤버의 기본 프로필 정보를 제공합니다.</p>
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "memberId": 7,
	 *   "nickname": "이동현",
	 *   "profileImageUrl": "https://cdn.example.com/profile7.jpg"
	 * }
	 * }</pre>
	 *
	 * @param memberId        참여자 ID
	 * @param nickname        참여자 닉네임
	 * @param profileImageUrl 참여자 프로필 이미지 URL
	 */

	public record ParticipantDetail(
		Long memberId,
		String nickname,
		String profileImageUrl
	) {
		@QueryProjection
		public ParticipantDetail {

		}
	}

	/**
	 * 내가 신청한 동출 게시글 리스트 조회 시 사용되는 DTO입니다.
	 * <p>
	 * 커서 기반 스크롤 API 응답에서 사용되며, 게시글의 기본 정보와 상태를 포함합니다.
	 * </p>
	 *
	 * @param fishingTripPostId      게시글 ID
	 * @param subject                게시글 제목
	 * @param fishingPointId         출조 포인트 ID
	 * @param fishingPointName       출조 포인트 대분류 이름
	 * @param fishingPointDetailName 출조 포인트 상세 이름
	 * @param fishingDate            출조 예정 일자
	 * @param createdAt              게시글 생성 시각
	 * @param currentCount           현재 모집된 인원 수
	 * @param recruitmentCount       총 모집 인원 수
	 * @param postStatus             게시글 상태 (RECRUITING or COMPLETED)
	 * @param commentCount           댓글 개수
	 * @param likeCount              좋아요 수
	 */
	public record MyFishingTripPostDetailPage(
		Long fishingTripPostId,
		String subject,
		Long fishingPointId,
		String fishingPointName,
		String fishingPointDetailName,
		ZonedDateTime fishingDate,
		ZonedDateTime createdAt,
		Integer currentCount,
		Integer recruitmentCount,
		PostStatus postStatus,
		Long commentCount,
		Long likeCount
	) {
		@QueryProjection
		public MyFishingTripPostDetailPage {

		}
	}

	public record HotPost(
		Long fishingTripPostId,
		String subject,
		Long regionId,
		RegionType regionType,
		String imageUrl,
		Long hotScore,
		Long likeCount,
		Long commentCount
	) {
	}

	public record HotPostDto(
		Long fishingTripPostId,
		String subject,
		Long regionId,
		RegionType regionType,
		List<Long> fileIdList,
		Long hotScore,
		Long likeCount,
		Long commentCount
	) {
		@QueryProjection
		public HotPostDto {

		}
	}
}

package com.backend.domain.fishingtrippost.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.region.entity.RegionType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class FishingTripPostResponse {

	/**
	 * 낚시 동행 게시글 상세 정보를 담는 응답 DTO입니다.
	 *
	 * <p>게시글의 기본 정보, 위치, 첨부 파일 URL 목록, 모집 상태 등을 포함하며,
	 * {@link DetailQueryDto}와 파일 URL 리스트를 기반으로 생성됩니다.</p>
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "fishingTripPostId": 1,
	 *   "name": "루피",
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
	 *   "fileUrlList": [
	 *     "https://cdn.example.com/image1.jpg",
	 *     "https://cdn.example.com/image2.jpg"
	 *   ],
	 *   "postStatus": "RECRUITING"
	 * }
	 * }</pre>
	 *
	 * @param fishingTripPostId 게시글 ID
	 * @param name 작성자 이름
	 * @param subject 게시글 제목
	 * @param content 게시글 내용
	 * @param currentCount 현재 참여 인원
	 * @param recruitmentCount 모집 정원
	 * @param createDate 게시글 생성일시 (ISO-8601 ZonedDateTime)
	 * @param fishingDate 출조 예정일시 (ISO-8601 ZonedDateTime)
	 * @param fishPointDetailName 낚시 포인트 상세명
	 * @param fishPointName 낚시 포인트 이름
	 * @param longitude 낚시 포인트 경도
	 * @param latitude 낚시 포인트 위도
	 * @param fileUrlList 첨부 이미지 URL 리스트
	 * @param postStatus 게시글 상태 (RECRUITING, COMPLETED 등)
	 */

	@Builder
	public record Detail(
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
		List<String> fileUrlList,
		PostStatus postStatus
	) {
		public static Detail fromDetailQueryDtoAndFileUrlList(DetailQueryDto detailQueryDto,
			List<String> fileUrlList) {
			return Detail.builder()
				.fishingTripPostId(detailQueryDto.fishingTripPostId())
				.name(detailQueryDto.name())
				.subject(detailQueryDto.subject())
				.content(detailQueryDto.content())
				.currentCount(detailQueryDto.currentCount())
				.recruitmentCount(detailQueryDto.recruitmentCount())
				.createDate(detailQueryDto.createDate())
				.fishingDate(detailQueryDto.fishingDate())
				.fishPointDetailName(detailQueryDto.fishPointDetailName())
				.fishPointName(detailQueryDto.fishPointName())
				.longitude(detailQueryDto.longitude())
				.latitude(detailQueryDto.latitude())
				.fileUrlList(fileUrlList)
				.postStatus(detailQueryDto.postStatus())
				.build();
		}
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
		PostStatus postStatus
	) {
		@QueryProjection
		public DetailQueryDto {

		}
	}

	/**
	 * 낚시 동행 게시글의 요약 정보를 클라이언트에 전달하기 위한 응답 DTO입니다.
	 *
	 * <p>사용자에게 보여줄 게시글의 제목, 내용, 출조일, 모집 인원 등의 정보를 포함하며,
	 * 대표 이미지 URL도 함께 전달됩니다.</p>
	 *
	 * <p>정렬 및 필터링과 함께 스크롤 페이징 목록에 활용됩니다.</p>
	 *
	 */
	//TODO 좋아요랑 댓글수도 추가해야함 추후에
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
		String imageUrl
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
		List<Long> fileIdList
	) {
		@QueryProjection
		public DetailPageQueryDto {
		}
	}
}

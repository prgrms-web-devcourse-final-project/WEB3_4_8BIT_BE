package com.backend.domain.fishingtrippost.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.backend.domain.fishingtrippost.domain.PostStatus;
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
}

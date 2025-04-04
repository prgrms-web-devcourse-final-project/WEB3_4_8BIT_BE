package com.backend.domain.fishingtrippost.dto.response;

import java.util.List;

import lombok.Builder;

public class FishingTripPostResponse {

	/**
	 * 낚시 동행 게시글 상세 정보를 담는 응답 DTO입니다.
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "fishingTripPostId": 1,
	 *   "name": "루피",
	 *   "subject": "해적왕과 함께하는 낚시",
	 *   "content": "고무고무 낚시왕!",
	 *   "headCount": "2/4명",
	 *   "createDate": "2024.04.03",
	 *   "fishingDate": "2024.04.10",
	 *   "fishingTime": "06:00",
	 *   "fishPointDetailName": "동해 낚시 명소",
	 *   "fishPointName": "동해 포인트",
	 *   "longitude": 128.12345,
	 *   "latitude": 37.12345
	 * }
	 * }</pre>
	 *
	 * @param fishingTripPostId 게시글 ID
	 * @param name 작성자 이름
	 * @param subject 게시글 제목
	 * @param content 게시글 내용
	 * @param headCount "현재인원/모집인원명" 형식의 참가 인원 정보
	 * @param createDate 게시글 생성일 (yyyy.MM.dd)
	 * @param fishingDate 출조일 (yyyy.MM.dd)
	 * @param fishingTime 출조 시간 (HH:mm)
	 * @param fishPointDetailName 상세 낚시 포인트 이름
	 * @param fishPointName 낚시 포인트 이름
	 * @param longitude 경도
	 * @param latitude 위도
	 */
	@Builder
	public record Detail(
		Long fishingTripPostId,
		String name,
		String subject,
		String content,
		String headCount,
		String createDate,
		String fishingDate,
		String fishingTime,
		String fishPointDetailName,
		String fishPointName,
		Double longitude,
		Double latitude,
		List<Long> images
	) {
	}
}

package com.backend.domain.shipfishingpost.dto.response;

import java.time.LocalTime;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

public class ShipFishingPostResponse {

	/**
	 * {
	 * "shipFishingPostId": 1L,
	 * "subject": "게시글 제목",
	 * "content": "게시글 내용",
	 * "price": 80000
	 * "imageList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 * "startTime": "15:00"
	 * "durationTime": "02:30"
	 * "maxGuestCount": 10
	 * "reviewEverRate": 4.7
	 * }
	 *
	 * @param shipFishingPostId 게시글 번호
	 * @param subject 게시글 제목
	 * @param content 게시글 내용
	 * @param price 프로그램 금액
	 * @param imageList 게시글 첨부 이미지 리스트
	 * @param startTime 프로그램 시작 시간
	 * @param durationTime 프로그램 진행 시간
	 * @param maxGuestCount 최대 인원 수
	 * @param reviewEverRate 평점
	 */
	public record Detail(
		Long shipFishingPostId,
		String subject,
		String content,
		Long price,
		List<String> imageList,
		LocalTime startTime,
		LocalTime durationTime,
		Long maxGuestCount,
		Double reviewEverRate) {
		@QueryProjection
		public Detail {
		}
	}
}

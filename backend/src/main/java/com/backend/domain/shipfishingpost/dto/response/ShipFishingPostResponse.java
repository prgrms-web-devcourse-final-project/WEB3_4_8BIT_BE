package com.backend.domain.shipfishingpost.dto.response;

import java.time.LocalTime;
import java.util.List;

import com.backend.domain.ship.dto.response.ShipResponse;
import com.querydsl.core.annotations.QueryProjection;

public class ShipFishingPostResponse {

	/**
	 * {
	 *   "shipFishingPostId": 1L,
	 *   "subject": "게시글 제목",
	 *   "content": "게시글 내용",
	 *   "price": 80000
	 *   "imageList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 *   "startTime": "15:00"
	 *   "durationTime": "02:30"
	 *   "maxGuestCount": 10
	 *   "reviewEverRate": 4.7
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
		// Todo : 추후 찜하기 반영
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

	/**
	 * {
	 *     detailShipFishingPost
	 *     detailShip
	 * }
	 *
	 * @param detailShipFishingPost
	 * @param detailShip
	 */
	public record DetailAll(
		ShipFishingPostResponse.Detail detailShipFishingPost,
		ShipResponse.Detail detailShip
		// Todo : user 정보 추후 작성 예정
	) {
	}

	/**
	 *{
	 *   "shipFishingPostId": 1,
	 *   "subject": "게시글 제목",
	 *   "location": "서울",
	 *   "price": 80000,
	 *   "startTime": "15:00",
	 *   "endTime": "17:00",
	 *   "durationTime": "02:00",
	 *   "imageList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 *   "reviewEverRate": 4.7
	 * }
	 *
	 * @param shipFishingPostId - 게시글 id
	 * @param subject - 게시글 제목
	 * @param location - 지역
	 * @param price - 금액
	 * @param startTime - 시작시간
	 * @param endTime - 종료시간
	 * @param durationTime - 소요 시간
	 * @param imageList - 사진 url
	 * @param reviewEverRate - 평점
	 */
	public record DetailPage(
		Long shipFishingPostId,
		String subject,
		String location,
		Long price,
		LocalTime startTime,
		LocalTime endTime,
		LocalTime durationTime,
		// List<String> fishList,
		List<String> imageList,
		Double reviewEverRate
	) {
	}

}

package com.backend.domain.shipfishingpost.dto.response;

import java.time.LocalTime;
import java.util.List;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class ShipFishingPostResponse {

	/**
	 * {
	 *   "shipFishingPostId": 1L,
	 *   "subject": "게시글 제목",
	 *   "content": "게시글 내용",
	 *   "price": 80000
	 *   "fileIdList": [1, 2],
	 *   "fishIdList": [1, 2],
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
	 * @param fileIdList 게시글 첨부 이미지 리스트
	 * @param fishIdList 게시글 첨부 어종 리스트
	 * @param startTime 프로그램 시작 시간
	 * @param durationTime 프로그램 진행 시간
	 * @param maxGuestCount 최대 인원 수
	 * @param reviewEverRate 평점
	 */
	@Builder
	public record Detail(
		Long shipFishingPostId,
		String subject,
		String content,
		Long price,
		List<Long> fileIdList,
		List<Long> fishIdList,
		LocalTime startTime,
		LocalTime durationTime,
		Integer maxGuestCount,
		Double reviewEverRate) {
		@QueryProjection
		public Detail {
		}
	}

	/**
	 * {
	 *     detailShipFishingPost
	 *     detailShip
	 *     detailMember
	 * }
	 *
	 * @param detailShipFishingPost
	 * @param detailShip
	 * @param detailMember
	 */
	@Builder
	public record DetailAll(
		ShipFishingPostResponse.Detail detailShipFishingPost,
		ShipResponse.Detail detailShip,
		MemberResponse.ContactInfo detailMember
	) {
	}

	/**
	 * {
	 *   "shipFishingPostId": 1L,
	 *   "subject": "게시글 제목",
	 *   "content": "게시글 내용",
	 *   "price": 80000,
	 *   "fileUrlList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 *   "fishNameList": ["광어", "우럭"],
	 *   "startTime": "15:00",
	 *   "durationTime": "02:30",
	 *   "maxGuestCount": 10,
	 *   "reviewEverRate": 4.7,
	 *    detailShip,
	 *    detailMember,
	 * }
	 *
	 * @param shipFishingPostId 게시글 번호
	 * @param subject 게시글 제목
	 * @param content 게시글 내용
	 * @param price 프로그램 금액
	 * @param fileUrlList 게시글 첨부 이미지 리스트
	 * @param fishNameList 게시글 첨부 어종 리스트
	 * @param startTime 프로그램 시작 시간
	 * @param durationTime 프로그램 진행 시간
	 * @param maxGuestCount 최대 인원 수
	 * @param reviewEverRate 평점
	 * @param detailShip
	 * @param detailMember
	 */
	@Builder
	public record DetailWithFileUrlAndFishName(
		Long shipFishingPostId,
		String subject,
		String content,
		Long price,
		List<String> fileUrlList,
		List<String> fishNameList,
		LocalTime startTime,
		LocalTime durationTime,
		Integer maxGuestCount,
		Double reviewEverRate,
		ShipResponse.Detail detailShip,
		MemberResponse.ContactInfo detailMember
	) {
	}

	/**
	 *{
	 *   "shipFishingPostId": 1,
	 *   "subject": "게시글 제목",
	 *   "location": "서울",
	 *   "price": 80000,
	 *   "fileUrlList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 *   "fishNameList": ["광어", "우럭"],
	 *   "reviewEverRate": 4.7,
	 *   "reviewCount": 2
	 * }
	 *
	 * @param shipFishingPostId - 게시글 id
	 * @param subject - 게시글 제목
	 * @param location - 지역
	 * @param price - 금액
	 * @param fileUrlList - 사진 url 리스트
	 * @param fishNameList - 어류 Name 리스트
	 * @param reviewEverRate - 평점
	 * @param reviewCount - 댓글 수
	 */
	@Builder
	public record DetailScroll(
		Long shipFishingPostId,
		String subject,
		String location,
		Long price,
		List<String> fileUrlList,
		List<String> fishNameList,
		Double reviewEverRate,
		Long reviewCount
		// Todo : 위시리스트 반영
	) {
		public static DetailScroll fromDetailScroll(
			final ShipFishingPostResponse.DetailQueryDto detail,
			final List<String> fileUrlList,
			final List<String> fishNameList) {

			return DetailScroll.builder()
				.shipFishingPostId(detail.shipFishingPostId())
				.subject(detail.subject())
				.location(detail.location())
				.price(detail.price())
				.fileUrlList(fileUrlList)
				.fishNameList(fishNameList)
				.reviewEverRate(detail.reviewEverRate())
				.reviewCount(detail.reviewCount())
				.build();
		}
	}

	/**
	 *{
	 *   "shipFishingPostId": 1,
	 *   "subject": "게시글 제목",
	 *   "location": "서울",
	 *   "price": 80000,
	 *   "fileIdList": [1, 2],
	 *   "fishIdList": [1, 2],
	 *   "reviewEverRate": 4.7,
	 *   "reviewCount": 2
	 * }
	 *
	 * @param shipFishingPostId - 게시글 id
	 * @param subject - 게시글 제목
	 * @param location - 지역
	 * @param price - 금액
	 * @param fileIdList - 사진 url 리스트
	 * @param fishIdList - 어류 Name 리스트
	 * @param reviewEverRate - 평점
	 * @param reviewCount - 댓글 수
	 */
	@Builder
	public record DetailQueryDto(
		Long shipFishingPostId,
		String subject,
		String location,
		Long price,
		List<Long> fileIdList,
		List<Long> fishIdList,
		Double reviewEverRate,
		Long reviewCount
	) {
	}
}

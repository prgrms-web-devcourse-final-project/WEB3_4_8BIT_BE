package com.backend.domain.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import com.backend.domain.reservation.entity.ReservationStatus;

import lombok.Builder;

public class ReservationResponse {

	/**
	 * {
	 * 	 	"reservationId": 12345,
	 * 	 	"memberId": 67890,
	 * 	 	"reservationNumber": "20250402-202345",
	 * 	 	"guestCount": 4,
	 * 	 	"price": 50000,
	 * 	 	"totalPrice": 2000000,
	 * 	 	"reservationDate": "2025-04-01",
	 * 	 	"reservationStatus": "CONFIRMED"
	 * }
	 *
	 * @param reservationId - 예약 id
	 * @param memberId - 예약자 id
	 * @param reservationNumber - 예약 번호
	 * @param guestCount - 예약 인원
	 * @param price - 인당 가격
	 * @param totalPrice - 총 가격
	 * @param reservationDate - 예약 일자
	 * @param reservationStatus - 예약 상태
	 */
	@Builder
	public record Detail(
		Long reservationId,
		Long memberId,
		String reservationNumber,
		Integer guestCount,
		Long price,
		Long totalPrice,
		LocalDate reservationDate,
		ReservationStatus reservationStatus
	) {
	}

	/**
	 * {
	 * 	 	"reservationId": 12345,
	 * 	 	"shipFishingPostId": 1,
	 * 	 	"memberId": 67890,
	 * 	 	"name": "이름"
	 * 	 	"phone": "01012345678",
	 * 	 	"reservationNumber": "20250402-202345",
	 * 	 	"guestCount": 4,
	 * 	 	"price": 50000,
	 * 	 	"totalPrice": 2000000,
	 * 	 	"reservationDate": "2025-04-01",
	 * 	 	"reservationStatus": "CONFIRMED",
	 * 	 	"createdAt": "",
	 * 	 	"modifiedAt": ""
	 * }
	 *
	 * @param reservationId - 예약 id
	 * @param shipFishingPostId - 게시글 id
	 * @param memberId - 예약자 id
	 * @param name - 예약자 이름
	 * @param phone - 예약자 핸드폰 번호
	 * @param reservationNumber - 예약 번호
	 * @param guestCount - 예약 인원
	 * @param price - 인당 가격
	 * @param totalPrice - 총 가격
	 * @param reservationDate - 예약 일자
	 * @param reservationStatus - 예약 상태
	 * @param createdAt - 예약 생성 일자
	 * @param modifiedAt - 예약 수정 일자
	 */
	public record DetailWithMember(
		Long reservationId,
		Long shipFishingPostId,
		Long memberId,
		String name,
		String phone,
		String reservationNumber,
		Integer guestCount,
		Long price,
		Long totalPrice,
		LocalDate reservationDate,
		ReservationStatus reservationStatus,
		ZonedDateTime createdAt,
		ZonedDateTime modifiedAt
	) {
	}

	/**
	 * {
	 * 	 	"reservationId": 12345,
	 * 	 	"shipFishingPostId": 1,
	 * 	 	"name": "이름"
	 * 	 	"reservationNumber": "20250402-202345",
	 * 	 	"guestCount": 4,
	 * 	 	"reservationDate": "2025-04-01",
	 * 	 	"reservationStatus": "CONFIRMED",
	 * 	 	"createdAt": "",
	 * 	 	"modifiedAt": ""
	 * }
	 *
	 * @param reservationId - 예약 id
	 * @param shipFishingPostId - 게시글 id
	 * @param name - 예약자 이름
	 * @param reservationNumber - 예약 번호
	 * @param guestCount - 예약 인원
	 * @param reservationDate - 예약 일자
	 * @param reservationStatus - 예약 상태
	 * @param createdAt - 예약 생성 일자
	 * @param modifiedAt - 예약 수정 일자
	 */
	public record DetailWithName(
		Long reservationId,
		Long shipFishingPostId,
		String name,
		String reservationNumber,
		Integer guestCount,
		LocalDate reservationDate,
		ReservationStatus reservationStatus,
		ZonedDateTime createdAt,
		ZonedDateTime modifiedAt
	) {
	}

	/**
	 *
	 * @param reservationId
	 * @param shipFishingPostId
	 * @param reservationNumber
	 * @param subject
	 * @param reservationDate
	 * @param startTime
	 * @param location
	 * @param guestCount
	 * @param totalPrice
	 * @param reservationStatus
	 * @param fileIdList
	 * @param createdAt
	 */
	@Builder
	public record DetailQueryDto(
		Long reservationId,
		Long shipFishingPostId,
		String reservationNumber,
		String subject,
		LocalDate reservationDate,
		LocalTime startTime,
		String location,
		Integer guestCount,
		Long totalPrice,
		ReservationStatus reservationStatus,
		List<Long> fileIdList,
		ZonedDateTime createdAt
	) {
		public DetailQueryDto {
			fileIdList = (fileIdList == null) ? List.of() : fileIdList;
		}
	}

	/**
	 *
	 * @param reservationId
	 * @param shipFishingPostId
	 * @param reservationNumber
	 * @param subject
	 * @param reservationDate
	 * @param startTime
	 * @param location
	 * @param guestCount
	 * @param totalPrice
	 * @param reservationStatus
	 * @param fileUrlList
	 * @param createdAt
	 */
	@Builder
	public record DetailReservationList(
		Long reservationId,
		Long shipFishingPostId,
		String reservationNumber,
		String subject,
		LocalDate reservationDate,
		LocalTime startTime,
		String location,
		Integer guestCount,
		Long totalPrice,
		ReservationStatus reservationStatus,
		List<String> fileUrlList,
		ZonedDateTime createdAt
	) {
		public static DetailReservationList fromDetailReservationList(
			final ReservationResponse.DetailQueryDto detail,
			final List<String> fileUrlList
		) {
			return DetailReservationList.builder()
				.reservationId(detail.reservationId())
				.shipFishingPostId(detail.shipFishingPostId())
				.reservationNumber(detail.reservationNumber())
				.subject(detail.subject())
				.reservationDate(detail.reservationDate())
				.startTime(detail.startTime())
				.location(detail.location())
				.guestCount(detail.guestCount())
				.totalPrice(detail.totalPrice())
				.reservationStatus(detail.reservationStatus())
				.fileUrlList(fileUrlList)
				.createdAt(detail.createdAt())
				.build();
		}
	}

	/**
	 * {
	 *     "todayReservationCount": 5,
	 *     "recentReservationCount": 8,
	 *     "writtenPostCount": 3
	 * }
	 *
	 * @param todayReservationCount 오늘 예약 횟수
	 * @param recentReservationCount 다가오는 예약 횟수
	 * @param writtenPostCount 작성한 게시글 수
	 */
	@Builder
	public record DashBoard(
		Long todayReservationCount,
		Long recentReservationCount,
		Long writtenPostCount
	) {
		public static DashBoard fromDashBoard(
			final Long todayReservationCount,
			final Long recentReservationCount,
			final Long writtenPostCount) {

			return DashBoard.builder()
				.todayReservationCount(todayReservationCount)
				.recentReservationCount(recentReservationCount)
				.writtenPostCount(writtenPostCount)
				.build();
		}
	}
}

package com.backend.domain.reservation.dto.response;

import java.time.LocalDate;
import java.time.ZonedDateTime;

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
}

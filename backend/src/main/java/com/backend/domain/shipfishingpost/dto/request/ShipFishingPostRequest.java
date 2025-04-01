package com.backend.domain.shipfishingpost.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class ShipFishingPostRequest {

	/**
	 *{
	 * "subject": "게시글 제목",
	 * "content": "게시글 내용",
	 * "price": 80000,
	 * "location": "부산",
	 * "startTime": "15:00",
	 * "endTime": "17:30",
	 * "maxGuestCount": 10,
	 * "shipId": 10,
	 * "images": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
	 * "fishIds": [1, 2, 3],
	 * "unavailableDates": ["2025-03-25", "2025-03-26"]
	 *}
	 *
	 * @param subject "게시글 제목"
	 * @param content " 게시글 내용"
	 * @param price 80000
	 * @param location 출항 지역
	 * @param startTime 프로그램 시작시간
	 * @param endTime 프로그램 종료시간
	 * @param maxGuestCount 최대 인원 수
	 * @param shipId 게시글에 올릴 배 번호
	 * @param images 등록한 사진 리스트
	 * @param fishList 목적 어종 리스트
	 * @param unavailableDates 예약 불가능 날짜 리스트
	 */
	public record Create(
		@NotBlank(message = "게시글 제목은 필수 항목입니다.")
		@Size(max = 50, message = "게시글 제목은 최대 50자까지 가능합니다.")
		@Schema(description = "게시글 제목", example = "게시글 제목")
		String subject,

		@Size
		@NotBlank(message = "게시글 내용은 필수 항목입니다.")
		@Size(max = 800, message = "게시글 내용은 최대 800자까지 가능합니다.")
		@Schema(description = "게시글 내용", example = "게시글 내용")
		String content,

		@Positive(message = "금액은 0 이상이어야 합니다.")
		@Digits(integer = 8, fraction = 0, message = "금액은 최대 8자리(정수 8자리)까지 허용됩니다.")
		@Schema(description = "프로그램 금액", example = "80000")
		Long price,

		@NotBlank(message = "출항 지역은 필수 항목입니다.")
		@Schema(description = "출항 지역", example = "부산")
		String location,

		@NotNull(message = "시작 시간은 필수 항목입니다.")
		@Schema(description = "시작 시간", example = "15:00")
		@DateTimeFormat(pattern = "HH:mm")
		LocalTime startTime,

		@NotNull(message = "종료 시간은 필수 항목입니다.")
		@Schema(description = "종료 시간", example = "17:30")
		@DateTimeFormat(pattern = "HH:mm")
		LocalTime endTime,

		@Positive(message = "최대 인원은 0 명 이상이어야 합니다.")
		@Schema(description = "최대 인원", example = "10")
		Long maxGuestCount,

		@NotNull(message = "배 등록 번호는 필수 항목입니다.")
		@Schema(description = "배 Id 값 (entity)", example = "10")
		Long shipId,

		@Schema(description = "이미지 URL 리스트", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
		List<String> images,

		@Schema(description = "물고기 Id 리스트", example = "[1, 2, 3]")
		List<Long> fishList,

		@Schema(description = "예약 불가 날짜 리스트", example = "[\"2025-03-25\", \"2025-03-26\"]")
		List<LocalDate> unavailableDates
	) {
		public Create {
			images = (images == null) ? new ArrayList<>() : images;
			fishList = (fishList == null) ? new ArrayList<>() : fishList;
			unavailableDates = (unavailableDates == null) ? new ArrayList<>() : unavailableDates;
		}
	}

	/**
	 * 쿼리 파라미터 (검색 및 조건)
	 *
	 * @param minPrice - 최소 가격
	 * @param maxPrice - 최대 가격
	 * @param minRating - 최소 평점
	 * @param location - 지역
	 * @param fishId - 목적 어종
	 * @param searchDate - 예약 가능 날짜 ?
	 * @param keyword - 검색어
	 * @param guestCount - 인원 수
	 * @param duration - 소요 시간
	 */
	@Builder
	public record Search(
		@Schema(description = "최소 가격", example = "10000")
		Long minPrice,
		@Schema(description = "최대 가격", example = "500000")
		Long maxPrice,
		@Schema(description = "최소 평점", example = "4.5")
		Double minRating,
		@Schema(description = "위치 정보", example = "서울")
		String location,
		@Schema(description = "물고기 ID", example = "1")
		Long fishId,
		@Schema(description = "검색 날짜 (ISO 날짜 형식, 예: 2025-04-01)", example = "2025-04-01")
		LocalDate searchDate,
		@Schema(description = "검색 키워드", example = "낚시")
		String keyword,
		@Schema(description = "게스트 수", example = "2")
		Long guestCount,
		@Schema(description = "이용 시간 (ISO 시간 형식, 예: 01:30)", example = "01:30")
		LocalTime duration
	) {
	}

}
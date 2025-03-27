package com.backend.domain.shipfishposts.dto.request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShipFishPostsRequest {

	/**
	 *{
	 *   "subject": "게시글 제목",
	 *   "content": "게시글 내용",
	 *   "price": 80000,
	 *   "startDate": "2023-12-25T12:00:00Z",
	 *   "endDate": "2023-12-31T12:00:00Z",
	 *   "shipId": 10,
	 *   "images": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"]
	 *   "fishIds": [1, 2, 3]
	 *}
	 *
	 *
	 * @param subject "게시글 제목"
	 * @param content " 게시글 내용"
	 * @param price 80000
	 * @param startDate "2023-12-25T12:00:00Z"
	 * @param endDate 프로그램 종료시간
	 * @param shipId 게시글에 올릴 배 번호
	 * @param images 등록한 사진 리스트
	 * @param fishIds 목적 어종 리스트
	 *
	 */
	public record Create(
		@NotBlank(message = "게시글 제목은 필수 항목입니다.")
		@Schema(description = "게시글 제목", example = "게시글 제목")
		String subject,

		@NotBlank(message = "게시글 내용은 필수 항목입니다.")
		@Schema(description = "게시글 내용", example = "게시글 내용")
		String content,

		@Positive(message = "금액은 0 이상이어야 합니다.")
		@Schema(description = "프로그램 금액", example = "80000")
		BigDecimal price,

		@NotNull(message = "시작 시간은 필수 항목입니다.")
		@Schema(description = "시작 날짜 및 시간", example = "2023-12-25T12:00:00Z")
		ZonedDateTime startDate,

		@NotNull(message = "종료시간은 필수 항목입니다.")
		@Schema(description = "종료 날짜 및 시간", example = "2023-12-31T12:00:00Z")
		ZonedDateTime endDate,

		@NotNull(message = "배 등록 번호는 필수 항목입니다.")
		@Schema(description = "배 등록 번호 (Id 값)", example = "10")
		Long shipId,

		@Schema(description = "이미지 URL 리스트", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
		List<String> images,

		@Schema(description = "물고기 Id 리스트", example = "[1, 2, 3]")
		List<Long> fishIds) {
		public Create {
			images = (images == null) ? new ArrayList<>() : images;
			fishIds = (fishIds == null) ? new ArrayList<>() : fishIds;
		}
	}

}
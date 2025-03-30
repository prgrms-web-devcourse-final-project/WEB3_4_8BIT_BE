package com.backend.domain.review.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewRequest {

	/**
	 * {
	 *     "rating": 5,
	 *     "content": "진짜 손맛 제대로! 선상낚시 최고였어요 🎣",
	 *     "imageList": "[\"https://example.com/image1.jpg\", \"s3에 이미지 업로드 후 리턴 받은 URL\"]",
	 *     "shipFishingPostId": 1
	 * }
	 *
	 * @param rating	별점
	 * @param content	리뷰 내용
	 * @param imageList	이미지
	 * @param shipFishingPostId	선상 낚시 게시글 ID
	 * @author vdvhk12
	 */
	public record Create(
		@NotNull(message = "별점은 필수 항목입니다.")
		@Schema(description = "별점", example = "5")
		Integer rating,

		@NotBlank(message = "내용은 필수 항목입니다.")
		@Schema(description = "리뷰 내용", example = "진짜 손맛 제대로! 선상낚시 최고였어요 🎣")
		String content,

		@Schema(description = "이미지", example = "[\"https://example.com/image1.jpg\", \"s3에 이미지 업로드 후 리턴 받은 URL\"]")
		List<String> imageList,

		@NotNull(message = "선상 낚시 게시글 ID는 필수 항목입니다.")
		@Schema(description = "선상 낚시 게시글 ID", example = "1")
		Long shipFishingPostId
	) {}
}
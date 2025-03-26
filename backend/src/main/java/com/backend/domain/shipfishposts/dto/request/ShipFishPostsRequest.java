package com.backend.domain.shipfishposts.dto.request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShipFishPostsRequest {

	/**
	 *
	 * @param subject "게시글 제목"
	 * @param content " 게시글 내용"
	 * @param price 80000
	 * @param startDate "2023-12-25T12:00:00Z"
	 * @param endDate "2023-12-31T12:00:00Z"
	 * @param shipId 10
	 * @param images ["http://example.com/image1.jpg", "http://example.com/image2.jpg"]
	 * @param fishIds [1, 2, 3]
	 *
	 */

	public record Create(
		@NotNull @NotBlank String subject,
		@NotNull @NotBlank String content,
		@Positive BigDecimal price,
		@NotNull ZonedDateTime startDate,
		@NotNull ZonedDateTime endDate,
		@NotNull Long shipId,
		List<String> images,
		List<Long> fishIds
	) {
		public Create {
			images = (images == null) ? new ArrayList<>() : images;
			fishIds = (fishIds == null) ? new ArrayList<>() : fishIds;
		}
	}

}

package com.backend.domain.fishingtriprecruitment.dto.response;

import java.time.ZonedDateTime;

import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.querydsl.core.annotations.QueryProjection;

public class FishingTripRecruitmentResponse {
	public record DetailPage(
		Long fishingTripRecruitmentId,
		String nickname,
		String imageUrl,
		FishingLevel fishingLevel,
		String introduction,
		RecruitmentStatus recruitmentStatus,
		ZonedDateTime createdAt
	) {
		@QueryProjection
		public DetailPage {

		}
	}
}

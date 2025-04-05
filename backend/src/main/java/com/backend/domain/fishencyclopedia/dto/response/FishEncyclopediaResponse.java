package com.backend.domain.fishencyclopedia.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

public class FishEncyclopediaResponse {
	/**
	 * {
	 * "length": 15,
	 * "count": -12163,
	 * "fishPointName": "나로도",
	 * "fishPointDetailName": "내나로도 구룡마을 갯바위",
	 * "createdAt": "2024-07-16T13:09:08.663442+08:00"
	 * }
	 *
	 * @param length              잡은 물고기 길이
	 * @param count               잡은 횟수
	 * @param fishPointName       낚시 포인트 장소
	 * @param fishPointDetailName 낚시 포인트 상세 장소
	 * @param createdAt           잡은 날짜
	 */
	public record Detail(
		Long fishEncyclopediaId,
		Integer length,
		Integer count,
		String fishPointName,
		String fishPointDetailName,
		ZonedDateTime createdAt
	) {
		@QueryProjection
		public Detail {
		}
	}

	public record DetailPage(
		Long fishEncyclopediaId,
		String fileUrl,
		String fishName,
		Integer bestLength,
		Integer totalCount
	) {
		@QueryProjection
		public DetailPage {
		}
	}
}

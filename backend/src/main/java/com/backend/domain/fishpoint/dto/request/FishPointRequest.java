package com.backend.domain.fishpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FishPointRequest {

	public record Bounds(
		@Schema(description = "남서쪽(South-West) 위도", example = "33.1234")
		@NotNull(message = "swLat 값은 필수 항목입니다.")
		Double swLat,

		@Schema(description = "남서쪽(South-West) 경도", example = "126.5678")
		@NotNull(message = "swLng 값은 필수 항목입니다.")
		Double swLng,

		@Schema(description = "북동쪽(North-East) 위도", example = "34.5678")
		@NotNull(message = "neLat 값은 필수 항목입니다.")
		Double neLat,

		@Schema(description = "북동쪽(North-East) 경도", example = "127.1234")
		@NotNull(message = "neLng 값은 필수 항목입니다.")
		Double neLng
	) {}

	public record Nearby(
		@Schema(description = "현재 위치의 위도", example = "35.8714")
		@NotNull(message = "위도 값은 필수 항목입니다.")
		Double lat,

		@Schema(description = "현재 위치의 경도", example = "128.6014")
		@NotNull(message = "경도 값은 필수 항목입니다.")
		Double lng,

		@Schema(description = "조회 반경 (단위: km)", example = "5.0")
		Double radiusKm
	) {
		public Nearby(Double lat, Double lng, Double radiusKm) {
			this.lat = lat;
			this.lng = lng;
			this.radiusKm = (radiusKm == null) ? 5.0 : radiusKm;
		}
	}

	public record Search(
		@Schema(description = "검색할 지역명", example = "제주특별자치도")
		@NotBlank(message = "지역명은 필수 항목 입니다.")
		String region
	) {}
}

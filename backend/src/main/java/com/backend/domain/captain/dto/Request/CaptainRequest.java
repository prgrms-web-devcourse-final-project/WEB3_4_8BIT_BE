package com.backend.domain.captain.dto.Request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CaptainRequest {

	/**
	 * {@code
	 * {
	 *   "fileId": 101,
	 *   "nickname": "해적왕",
	 *   "description": "자기 소개글 내용",
	 *   "shipLicenseNumber": "1-2019123456",
	 *   "shipList": [1, 2, 3]
	 * }
	 *}
	 * @param fileId 프로필 이미지 ID (필수)
	 * @param nickname 사용자 닉네임 (필수, 최대 15자)
	 * @param description 자기 소개글 (필수,최대 500자)
	 * @param shipLicenseNumber 선장 면허 번호 (필수)
	 * @param shipList 등록할 배 ID 리스트 (필수, 최소 1개)
	 */

	public record Create(

		@NotBlank(message = "닉네임은 필수 항목입니다.")
		@Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
		@Schema(description = "선장 닉네임", example = "해적왕")
		String nickname,

		@Schema(description = "프로필 이미지 ID", example = "101")
		Long fileId,

		@NotBlank(message = "자기 소개글은 필수 항목입니다.")
		@Size(max = 500, message = "자개소개글은 최대 500자까지 가능합니다.")
		@Schema(description = "선장 자기 소개글", example = "해적왕이 되고싶은 루피 입니다.")
		String description,

		@NotBlank(message = "선박 운전 면허 번호는 필수 항목입니다.")
		@Size(max = 15, message = "선박 운전 면허 번호는 최대 15자까지 가능합니다.")
		@Schema(description = "선박 운전 면허 번호", example = "1-2019123456")
		String shipLicenseNumber,

		@NotEmpty(message = "배는 최소 1개 등록해야합니다.")
		@Schema(description = "소유한 배 Id 리스트 (entity)", example = "[1, 2, 3]")
		List<Long> shipList
	) {
	}

	//TODO 추후 선장 번호 수정 필요시 추가 예정

	/**
	 * {@code
	 * {
	 *   "shipList": [2, 3, 4]
	 * }
	 *}
	 * @param shipList 수정할 배 ID 리스트 (필수, 최소 1개)
	 */

	public record Update(
		@NotEmpty(message = "배는 최소 1개 등록해야합니다.")
		@Schema(description = "수정할 배 Id 리스트 (entity)", example = "[2, 3, 4]")
		List<Long> shipList
	) {
	}
}

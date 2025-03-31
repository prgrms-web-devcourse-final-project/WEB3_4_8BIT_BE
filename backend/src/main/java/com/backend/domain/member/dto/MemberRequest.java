package com.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequest {

	/**
	 * {
	 *   "nickname": "낚시중인낚시왕",
	 *   "profileImg": "http://example.com/profile.jpg",
	 *   "description": "낚시를 좋아합니다."
	 * }
	 *
	 * @param nickname 유저 닉네임 (최대 30자, 필수)
	 * @param profileImg 유저 프로필 이미지 URL (선택)
	 * @param description 유저 자기소개 (선택)
	 */
	public record form(
		@NotBlank(message = "닉네임은 필수 항목입니다.")
		@Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
		@Schema(description = "유저 닉네임", example = "낚시중인낚시왕")
		String nickname,

		@Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
		String profileImg,

		@NotBlank(message = "자기소개는 필수 항목입니다.")
		@Schema(description = "유저 자기소개", example = "낚시를 좋아합니다.")
		String description
	) {
	}
}
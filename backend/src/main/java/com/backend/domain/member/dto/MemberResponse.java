package com.backend.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class MemberResponse {

	/**
	 * {
	 *   "memberId": 1,
	 *   "email": "test@naver.com",
	 *   "name": "홍길동",
	 *   "nickname": "테스트닉",
	 *   "phone": "010-1234-5678",
	 *   "profileImg": "http://example.com/profile.jpg",
	 *   "description": "자기소개입니다.",
	 * }
	 *
	 * @param memberId 회원 ID
	 * @param email 이메일
	 * @param name 이름
	 * @param nickname 닉네임
	 * @param phone 전화번호
	 * @param profileImg 프로필 이미지 URL
	 * @param description 자기소개
	 */
	@Builder
	public record Detail(
		Long memberId,
		String email,
		String name,
		String nickname,
		String phone,
		String profileImg,
		String description
	) {
		@QueryProjection
		public Detail {
		}
	}

	/**
	 * {
	 *   "memberId": 1,
	 *   "email": "test@naver.com",
	 *   "name": "홍길동",
	 *   "phone": "010-1234-5678",
	 * }
	 *
	 * @param memberId
	 * @param email
	 * @param name
	 * @param phone
	 */
	@Builder
	public record ContactInfo(
		Long memberId,
		String email,
		String name,
		String phone
	) {
	}
}

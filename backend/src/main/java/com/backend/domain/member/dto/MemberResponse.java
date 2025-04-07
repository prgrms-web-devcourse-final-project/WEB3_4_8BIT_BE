package com.backend.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class MemberResponse {

	/**
	 * 회원 상세 정보를 담는 응답 DTO입니다.
	 *
	 * <p>예시 JSON 응답 형태:</p>
	 * <pre>{@code
	 * {
	 *   "memberId": 1,
	 *   "email": "test@naver.com",
	 *   "name": "홍길동",
	 *   "nickname": "테스트닉",
	 *   "phone": "010-1234-5678",
	 *   "fileUrl": "http://example.com/profile.jpg",
	 *   "description": "자기소개입니다.",
	 *   "isAddInfo": true
	 * }
	 * }</pre>
	 *
	 * @param memberId 회원 ID
	 * @param email 이메일
	 * @param name 이름
	 * @param nickname 닉네임
	 * @param phone 전화번호
	 * @param fileUrl 프로필 이미지 URL
	 * @param description 자기소개
	 * @param isAddInfo 추가 정보 입력 여부 (true: 추가 정보 입력됨, false: 미입력)
	 */
	@Builder
	public record Detail(
		Long memberId,
		String email,
		String name,
		String nickname,
		String phone,
		String fileUrl,
		String description,
		Boolean isAddInfo
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

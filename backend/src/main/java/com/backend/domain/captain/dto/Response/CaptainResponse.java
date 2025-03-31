package com.backend.domain.captain.dto.Response;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public class CaptainResponse {

	/**
	 * {@code
	 *   "memberId": 1,
	 *   "email": "test@naver.com",
	 *   "name": "루피",
	 *   "nickname": "해적왕",
	 *   "phone": "010-1234-5678",
	 *   "profileImg": "http://example.com/profile.jpg",
	 *   "description": "해적왕이 되고싶은 루피 입니다.",
	 *   "shipLicenseNumber": "1-2019123456",
	 *   "shipList": [1L, 2L, 3L]
	 * }
	 *
	 * @param memberId 회원 ID
	 * @param email 이메일
	 * @param name 이름
	 * @param nickname 닉네임
	 * @param phone 전화번호
	 * @param profileImg 프로필 이미지 URL
	 * @param description 자기소개
	 * @param shipLicenseNumber 선박 운전 면허 번호
	 * @param shipList 선장 보유 배 Id 목록
	 */
	@Builder
	public record Detail(
		Long memberId,
		String email,
		String name,
		String nickname,
		String phone,
		String profileImg,
		String description,
		String shipLicenseNumber,
		List<Long> shipList
	) {
		@QueryProjection
		public Detail {

		}
	}

	/**
	 * {@code
	 *   "nickname": "해적왕",
	 *   "profileImg": "http://example.com/profile.jpg",
	 *   "role": "CAPTAIN",
	 *   "isAddInfo": "true"
	 * }
	 *
	 * @param nickname 선장 닉네임
	 * @param profileImg 프로필 이미지 URL
	 * @param role 사용자 역할 (CAPTAIN)
	 * @param isAddInfo 추가정보 입력 여부
	 */
	//TODO 추후 로직에 따라 적용 여부 판별

	//
	// @Builder
	// public record Login(
	// 	String nickname,
	// 	String profileImg,
	// 	String role,
	// 	boolean isAddInfo
	// ) {
	//
	// }
}

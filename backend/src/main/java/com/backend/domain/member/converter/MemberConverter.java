package com.backend.domain.member.converter;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;

public class MemberConverter {

	/**
	 * Member 엔티티를 MemberResponse.Detail DTO로 변환하는 메서드
	 *
	 * @param member 변환할 Member 엔티티
	 * @return 변환된 MemberResponse.Detail 객체
	 */
	public static MemberResponse.Detail fromMemberToDetail(final Member member) {
		return MemberResponse.Detail.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.name(member.getName())
			.phone(member.getPhone())
			.description(member.getDescription())
			.nickname(member.getNickname())
			.profileImg(member.getProfileImg())
			.build();
	}
}

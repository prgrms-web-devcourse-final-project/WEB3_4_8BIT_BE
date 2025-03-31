package com.backend.domain.captain.converter;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.member.entity.Member;

public class CaptainConverter {

	/**
	 * 회원가입된 멤버 정보 및 선장 생성 Dto를 Entity로 변환 메서드
	 *
	 * @param requestDto {@link CaptainRequest.Create}
	 * @param member {@link Member}
	 * @return {@link Captain}
	 */
	public static Captain fromMemberAndCaptainRequestCreate(CaptainRequest.Create requestDto, Member member) {
		return Captain.builder()
			.memberId(member.getMemberId())
			.shipLicenseNumber(requestDto.shipLicenseNumber())
			.shipList(requestDto.shipList())
			.build();
	}

}

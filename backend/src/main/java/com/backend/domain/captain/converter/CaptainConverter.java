package com.backend.domain.captain.converter;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.entity.Captain;

public class CaptainConverter {

	/**
	 * 회원가입된 멤버 Id 및 선장 생성 Dto를 Entity로 변환 메서드
	 *
	 * @param memberId   {@link Long}
	 * @param requestDto {@link CaptainRequest.Create}
	 * @return {@link Captain}
	 */
	public static Captain fromMemberAndCaptainRequestCreate(
		final Long memberId,
		final CaptainRequest.Create requestDto
	) {
		return Captain.builder()
			.memberId(memberId)
			.shipLicenseNumber(requestDto.shipLicenseNumber())
			.shipList(requestDto.shipList())
			.build();
	}

}

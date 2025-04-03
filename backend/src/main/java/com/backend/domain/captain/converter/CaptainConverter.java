package com.backend.domain.captain.converter;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.entity.Captain;

public class CaptainConverter {

	/**
	 * 회원가입된 멤버 Id 및 선장 생성 Dto를 Entity로 변환 메서드
	 *
	 * @param requestDto {@link CaptainRequest.Create}
	 * @param memberId {@link Long}
	 * @return {@link Captain}
	 */
	public static Captain fromMemberAndCaptainRequestCreate(
		CaptainRequest.Create requestDto,
		Long memberId
	) {
		return Captain.builder()
			.memberId(memberId)
			.shipLicenseNumber(requestDto.shipLicenseNumber())
			.shipList(requestDto.shipList())
			.build();
	}

}

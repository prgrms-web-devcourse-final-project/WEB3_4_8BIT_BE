package com.backend.domain.catchmaxlength.converter;

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;

public class CatchMaxLengthConvert {

	public static CatchMaxLength fromCatchMaxLength(Long fishId, Long memberId) {
		return CatchMaxLength.builder()
				.fishId(fishId)
				.memberId(memberId)
				.build();
	}
}

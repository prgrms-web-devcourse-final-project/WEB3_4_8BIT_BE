package com.backend.global.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TossCancelRequest(
	String cancelReason,
	Long cancelAmount
) {
	public static TossCancelRequest of(final String cancelReason, final Long cancelAmount) {
		return TossCancelRequest.builder()
			.cancelReason(cancelReason)
			.cancelAmount(cancelAmount)
			.build();
	}
}

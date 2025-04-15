package com.backend.global.payment.dto.response;

import java.time.OffsetDateTime;

public record TossCancelResponse(
	String status,
	String cancelReason,
	Long canceledAmount,
	OffsetDateTime canceledAt
) {
}

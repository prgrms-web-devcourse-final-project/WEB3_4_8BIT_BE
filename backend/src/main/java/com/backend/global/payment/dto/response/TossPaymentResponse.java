package com.backend.global.payment.dto.response;

import java.time.OffsetDateTime;

import lombok.Getter;

@Getter
public class TossPaymentResponse {
	private String paymentKey;
	private String orderId;
	private String method;
	private OffsetDateTime approvedAt;
	private Long totalAmount;
	private String status;
	private Card card;
	private Receipt receipt;

	@Getter
	public static class Card {
		private String number;
		private String approveNo;
	}

	@Getter
	public static class Receipt {
		private String url;
	}
}
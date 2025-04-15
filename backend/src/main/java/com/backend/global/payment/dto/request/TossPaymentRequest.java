package com.backend.global.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 토스 결제 요청
 *
 * @param paymentKey
 * @param amount
 * @param orderId
 */
@Builder
public record TossPaymentRequest(
	@NotBlank
	@Schema(description = "토스에서 발급하는 결제 고유 식별자")
	String paymentKey,

	@NotNull
	@Min(0)
	@Schema(description = "결제 금액")
	Long amount,

	@NotBlank
	@Size(min = 6, max = 64)
	@Schema(description = "주문번호, 결제 요청에서 직접 생성한 영문 대소문자, 숫자, '-', '_'로 이루어진 6자 이상 64이하 문자열")
	String orderId
) {
	public static TossPaymentRequest from(
		final String paymentKey,
		final Long amount,
		final String orderId) {

		return TossPaymentRequest.builder()
			.paymentKey(paymentKey)
			.amount(amount)
			.orderId(orderId)
			.build();
	}
}


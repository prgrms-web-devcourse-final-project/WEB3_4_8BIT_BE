package com.backend.global.payment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.backend.global.payment.dto.request.TossCancelRequest;
import com.backend.global.payment.dto.request.TossPaymentRequest;
import com.backend.global.payment.dto.response.TossCancelResponse;
import com.backend.global.payment.dto.response.TossPaymentResponse;
import com.backend.global.payment.exception.PaymentErrorCode;
import com.backend.global.payment.exception.PaymentException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentHttpClient {

	private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${api.toss.secret-key}")
	private String tossApiSecretKey;

	public TossPaymentResponse sendPaymentConfirmRequest(final TossPaymentRequest request) {
		String confirmUrl = TOSS_PAYMENT_URL + "/confirm";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(tossApiSecretKey, "");

		HttpEntity<TossPaymentRequest> entity = new HttpEntity<>(request, headers);

		try {
			ResponseEntity<TossPaymentResponse> responseEntity = restTemplate.exchange(
				confirmUrl,
				HttpMethod.POST,
				entity,
				TossPaymentResponse.class
			);
			return responseEntity.getBody();
		} catch (HttpStatusCodeException e) {
			handleApiError(e);
			throw new PaymentException(PaymentErrorCode.TOSS_API_ERROR);
		}
	}

	public TossCancelResponse cancelPayment(final String paymentKey, final String cancelReason, final Long amount) {
		String cancelUrl = TOSS_PAYMENT_URL + "/" + paymentKey + "/cancel";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(tossApiSecretKey, "");

		TossCancelRequest cancelRequest = TossCancelRequest.of(cancelReason, amount);

		HttpEntity<TossCancelRequest> entity = new HttpEntity<>(cancelRequest, headers);

		try {
			ResponseEntity<TossCancelResponse> response = restTemplate.exchange(
				cancelUrl,
				HttpMethod.POST,
				entity,
				TossCancelResponse.class
			);
			log.info("Toss 결제 취소 완료: paymentKey={}, amount={}", paymentKey, amount);

			return response.getBody();
		} catch (HttpStatusCodeException e) {
			log.error("Toss 결제 취소 실패: {}", e.getResponseBodyAsString());

			throw new PaymentException(PaymentErrorCode.TOSS_API_CANCEL_FAILED);
		}
	}

	private void handleApiError(HttpStatusCodeException e) {
		try {
			Map<String, Object> errorResponse = objectMapper
				.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});

			log.error("Toss API Error: code={}, message={}", errorResponse.get("code"), errorResponse.get("message"));
		} catch (Exception ex) {
			log.error("Failed to parse Toss API error", ex);
		}
	}
}

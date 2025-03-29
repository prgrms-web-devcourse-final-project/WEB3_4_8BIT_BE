package com.backend.domain.review.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ReviewErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 *
 * @author vdvhk12
 */
@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {

	DUPLICATE_REVIEW(HttpStatus.CONFLICT, 4091, "리뷰는 동일한 예약에 대해 한 번만 작성할 수 있습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
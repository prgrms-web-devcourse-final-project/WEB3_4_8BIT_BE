package com.backend.domain.review.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

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
public enum ReviewErrorCode implements ErrorCode {

	//선상 낚시 리뷰 에러 코드 400
	DUPLICATE_REVIEW(HttpStatus.CONFLICT, 4001, "리뷰는 동일한 예약에 대해 한 번만 작성할 수 있습니다."),
	NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, 4002, "해당 리뷰를 찾을 수 없습니다."),
	FORBIDDEN_REVIEW_DELETE(HttpStatus.FORBIDDEN, 4003, "리뷰 삭제 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}
package com.backend.domain.fishingtriprecruitment.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishingTripRecruitmentErrorCode implements ErrorCode {

	FISHING_TRIP_RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 1401, "해당 동출 모집신청을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

package com.backend.domain.fishingtriprecruitment.exception;

import org.springframework.http.HttpStatus;

import com.backend.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FishingTripRecruitmentErrorCode implements ErrorCode {

	FISHING_TRIP_RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 14001, "해당 동출 모집신청을 찾을 수 없습니다."),
	FISHING_TRIP_RECRUITMENT_UNAUTHORIZED(HttpStatus.FORBIDDEN, 14002, "해당 동출 신청 현황에 접근 권한이 없습니다."),
	FISHING_TRIP_RECRUITMENT_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, 14003, "이미 해당 게시글에 신청한 회원입니다."),
	FISHING_TRIP_RECRUITMENT_AUTHOR_DO_NOT_APPLIED(HttpStatus.BAD_REQUEST, 14004, "게시글 작성자는 신청할 수 없습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
}

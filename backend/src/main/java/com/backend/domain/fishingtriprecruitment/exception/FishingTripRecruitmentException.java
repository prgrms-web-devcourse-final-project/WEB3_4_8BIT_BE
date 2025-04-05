package com.backend.domain.fishingtriprecruitment.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

public class FishingTripRecruitmentException extends GlobalException {
	private final ErrorCode errorCode;

	public FishingTripRecruitmentException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public FishingTripRecruitmentException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

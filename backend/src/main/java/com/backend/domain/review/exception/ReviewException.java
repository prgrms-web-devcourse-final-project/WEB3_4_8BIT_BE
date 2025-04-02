package com.backend.domain.review.exception;

import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.Getter;

/**
 * ReviewException
 * <p>리뷰 예외 클래스 입니다. <br><br>
 * 사용 예시: </p>
 * {@code
 * throw new ReviewException(ReviewErrorCode.);
 * }
 * @author vdvhk12
 */
@Getter
public class ReviewException extends GlobalException {

	private final ErrorCode errorCode;

	/**
	 * ReviewException 생성자 입니다.
	 * @param errorCode ReviewErrorCode 값
	 */
	public ReviewException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public ReviewException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
		this.errorCode = errorCode;
	}
}

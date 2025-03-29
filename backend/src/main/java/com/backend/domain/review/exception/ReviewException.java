package com.backend.domain.review.exception;

import org.springframework.http.HttpStatus;

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
public class ReviewException extends RuntimeException {

	private final ReviewErrorCode reviewErrorCode;

	/**
	 * ReviewException 생성자 입니다.
	 * @param reviewErrorCode ReviewErrorCode 값
	 */
	public ReviewException(ReviewErrorCode reviewErrorCode) {
		super(reviewErrorCode.getMessage());
		this.reviewErrorCode = reviewErrorCode;
	}

	/**
	 * 응답 HttpStatus를 반환하는 메서드 입니다.
	 * @return {@link HttpStatus}
	 */
	public HttpStatus getStatus() {
		return reviewErrorCode.getHttpStatus();
	}
}

package com.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * GlobalException
 * <p>공통으로 사용할 예외 클래스 입니다. <br><br>
 * 사용 예시: </p>
 * {@code
 * throw new GlobalException(GlobalErrorCode.NOT_VALID);
 * }
 * @author Kim Dong O
 */
@Getter
public class GlobalException extends RuntimeException {
	private final ErrorCode errorCode;

	/**
	 * GlobalException 생성자 입니다.
	 * @param errorCode GlobalErrorCode 값
	 */
	public GlobalException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	/**
	 * GlobalException 생성자 입니다.
	 * @param errorCode GlobalErrorCode 값
	 * @param cause 상위 Exception
	 */
	public GlobalException(Throwable cause, ErrorCode errorCode) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	/**
	 * 응답 HttpStatus를 반환하는 메서드 입니다.
	 * @return {@link HttpStatus}
	 */
	public HttpStatus getStatus() {
		return errorCode.getHttpStatus();
	}
}
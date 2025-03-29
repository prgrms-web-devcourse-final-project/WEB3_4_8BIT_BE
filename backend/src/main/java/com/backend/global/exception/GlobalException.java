package com.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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
	private final GlobalErrorCode globalErrorCode;

	/**
	 * GlobalException 생성자 입니다.
	 * @param globalErrorCode GlobalErrorCode 값
	 */
	public GlobalException(GlobalErrorCode globalErrorCode) {
		super(globalErrorCode.getMessage());
		this.globalErrorCode = globalErrorCode;
	}

	/**
	 * 응답 HttpStatus를 반환하는 메서드 입니다.
	 * @return {@link HttpStatus}
	 */
	public HttpStatus getStatus() {
		return globalErrorCode.getHttpStatus();
	}
}
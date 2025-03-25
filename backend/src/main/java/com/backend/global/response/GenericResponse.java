package com.backend.global.response;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * GenericResponse
 * <p>공통 응답 객체 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
public class GenericResponse<T> {

	private final ZonedDateTime timestamp;
	private final boolean isSuccess;
	private final int code;
	private final T data;
	private final String message;

	@Builder(access = AccessLevel.PRIVATE)
	private GenericResponse(boolean isSuccess, int code, T data, String message) {
		this.timestamp = ZonedDateTime.now();
		this.isSuccess = isSuccess;
		this.code = code;
		this.data = data;
		this.message = message;
	}

	/**
	 * 요청이 성공하고 code, data, message 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param data    반환 데이터
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(int code, T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.data(data)
			.message(message)
			.build();
	}

	/**
	 * 요청이 성공하고 code, data 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param data    반환 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(int code, T data) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.data(data)
			.build();
	}

	/**
	 * 요청이 성공하고 code, data, message 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(int code, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.message(message)
			.build();
	}

	/**
	 * 요청이 성공하고 code, data, message 있을 때
	 *
	 * @param code    응답 코드 값
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(int code) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.build();
	}

	/**
	 * 요청이 성공하고 data, message 있을 때
	 * <p>code 기본 값 : 200</p>
	 *
	 * @param data    반환 데이터
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(HttpStatus.OK.value())
			.message(message)
			.data(data)
			.build();
	}

	/**
	 * 요청이 성공하고 message 있을 때
	 * <p>code 기본 값 : 200</p>
	 *
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(String message) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(HttpStatus.OK.value())
			.message(message)
			.build();
	}

	/**
	 * 요청이 성공하고 data 있을 때
	 * <p>code 기본 값 : 200</p>
	 *
	 * @param data    반환 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok(T data) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(HttpStatus.OK.value())
			.data(data)
			.build();
	}

	/**
	 * 요청이 성공했을 때
	 * <p>code 기본 값 : 200</p>
	 *
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> ok() {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(HttpStatus.OK.value())
			.build();
	}

	/**
	 * 요청이 실패하고 code, data, message 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param data    반환 데이터
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(int code, T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.data(data)
			.message(message)
			.build();
	}

	/**
	 * 요청이 실패하고 code, data 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param data    반환 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(int code, T data) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.data(data)
			.build();
	}

	/**
	 * 요청이 실패하고 code, message 있을 때
	 *
	 * @param code    응답 코드 값
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(int code, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.message(message)
			.build();
	}

	/**
	 * 요청이 실패하고 code 있을 때
	 *
	 * @param code    응답 코드 값
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(int code) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.build();
	}

	/**
	 * 요청이 실패하고 data, message 있을 때
	 * <p>code 기본 값 : 400</p>
	 *
	 * @param data    반환 데이터
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(HttpStatus.BAD_REQUEST.value())
			.message(message)
			.data(data)
			.build();
	}

	/**
	 * 요청이 실패하고 message 있을 때
	 * <p>code 기본 값 : 400</p>
	 *
	 * @param message 반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(HttpStatus.BAD_REQUEST.value())
			.message(message)
			.build();
	}

	/**
	 * 요청이 실패하고 data 있을 때
	 * <p>code 기본 값 : 400</p>
	 *
	 * @param data    반환 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(T data) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(HttpStatus.BAD_REQUEST.value())
			.data(data)
			.build();
	}

	/**
	 * 요청이 실패했을 때
	 * <p>code 기본 값 : 400</p>
	 *
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail() {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(HttpStatus.BAD_REQUEST.value())
			.build();
	}
}
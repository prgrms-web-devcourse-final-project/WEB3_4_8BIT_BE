package com.backend.global.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * GenericResponse
 * <p>공통 응답 객체 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GenericResponse<T> {

	@Builder.Default
	@Schema(example = "2025-03-27T15:19:31.902266+09:00", description = "요청 시간")
	private final ZonedDateTime timestamp = ZonedDateTime.now();

	@Schema(example = "true", description = "성공 여부")
	private final boolean isSuccess;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "1001", description = "커스텀 코드 (예외 발생시 사용), (없을 시 반환 X)")
	private final Integer code;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "\"testData\": \"testData\"", description = "응답 데이터 (없을시 반환 X)")
	private final T data;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "testMessage", description = "응답 데이터 (없을시 반환 X)")
	private final String message;

	/**
	 * isSuccess, data, message 있을 때
	 *
	 * @param isSuccess 성공 여부
	 * @param data      반환 데이터
	 * @param message   반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> of(boolean isSuccess, T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(isSuccess)
			.data(data)
			.message(message)
			.build();
	}

	/**
	 * isSuccess, data 있을 때
	 *
	 * @param isSuccess 성공 여부
	 * @param data      반환 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> of(boolean isSuccess, T data) {
		return GenericResponse.<T>builder()
			.isSuccess(isSuccess)
			.data(data)
			.build();
	}

	/**
	 * isSuccess, message 있을 때
	 *
	 * @param isSuccess 성공 여부
	 * @param message   반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> of(boolean isSuccess, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(isSuccess)
			.message(message)
			.build();
	}

	/**
	 * isSuccess 있을 때
	 *
	 * @param isSuccess 성공 여부
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> of(boolean isSuccess) {
		return GenericResponse.<T>builder()
			.isSuccess(isSuccess)
			.build();
	}

	/**
	 * isSuccess, message 있을 때
	 * @param data      응답 데이터
	 * @param message   반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.message(message)
			.build();
	}

	/**
	 * isSuccess, message 있을 때
	 * @param data      응답 데이터
	 * @param code      응답 코드
	 * @param message   반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(Integer code, T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.data(data)
			.message(message)
			.build();
	}

	/**
	 * isSuccess, message 있을 때
	 * @param code      응답 코드
	 * @param message   반환 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T> GenericResponse<T> fail(Integer code, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.code(code)
			.message(message)
			.build();
	}
}

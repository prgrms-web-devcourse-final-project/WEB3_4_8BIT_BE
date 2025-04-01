package com.backend.global.dto.response;

/**
 * ErrorDetail
 * <p>@NotNull등과 같은 필드 에러 발생시 처리할 클래스</p>
 *
 * @param field
 * @param reason
 * @author Kim Dong O
 */
public record ErrorDetail(String field, String reason) {

	/**
	 * ErrorDetail 생성 팩토리 메서드
	 *
	 * @param field  예외가 발생한 필드 이름
	 * @param reason 예외가 발생한 이유
	 * @return {@link ErrorDetail}
	 */
	public static ErrorDetail of(String field, String reason) {
		return new ErrorDetail(field, reason);
	}
}
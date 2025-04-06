package com.backend.global.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = {
	EnumValidatorForString.class,
	EnumValidatorForEnum.class
})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEnum {

	String message() default "요청 값이 유효하지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * 검증할 enum 클래스
	 */
	Class<? extends Enum<?>> enumClass();

	/**
	 * null 허용 여부
	 */
	boolean nullable() default false;
}

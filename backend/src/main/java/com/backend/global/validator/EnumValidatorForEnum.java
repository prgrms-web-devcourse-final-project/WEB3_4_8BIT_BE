package com.backend.global.validator;

import java.lang.reflect.Method;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumValidatorForEnum implements ConstraintValidator<ValidEnum, Enum<?>> {

	private ValidEnum validEnum;

	@Override
	public void initialize(ValidEnum annotation) {
		this.validEnum = annotation;
	}

	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
		if (value == null) {
			return validEnum.nullable();
		}

		Class<? extends Enum<?>> enumClass = validEnum.enumClass();
		Object[] enumConstants = enumClass.getEnumConstants();

		Method getDisplayNameMethod = null;
		try {
			getDisplayNameMethod = enumClass.getMethod("getDisplayName");
		} catch (NoSuchMethodException e) {
			log.debug("[EnumValidator] '{}' enum getDisplayName() 메서드가 없어 무시합니다.",
				enumClass.getSimpleName());
		}

		for (Object constant : enumConstants) {
			Enum<?> enumConstant = (Enum<?>)constant;

			// 기본적으로 name() 으로 비교
			if (enumConstant.name().equals(value.name())) {
				return true;
			}

			// getDisplayName() 이 존재하면 비교
			if (getDisplayNameMethod != null) {
				try {
					Object displayName = getDisplayNameMethod.invoke(enumConstant);
					if (Objects.equals(displayName, value.toString())) {
						return true;
					}
				} catch (Exception e) {
					log.warn("[EnumValidator] '{}' enum getDisplayName() 호출 중 예외 발생",
						enumClass.getSimpleName(), e);
				}
			}
		}

		return false;
	}
}

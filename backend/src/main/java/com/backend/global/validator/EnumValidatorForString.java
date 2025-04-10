package com.backend.global.validator;

import java.lang.reflect.Method;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumValidatorForString implements ConstraintValidator<ValidEnum, String> {

	private ValidEnum validEnum;

	@Override
	public void initialize(ValidEnum annotation) {
		this.validEnum = annotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return validEnum.nullable();
		}

		Object[] enumConstants = validEnum.enumClass().getEnumConstants();
		if (enumConstants == null) {
			return false;
		}

		Method getDisplayNameMethod = null;
		try {
			getDisplayNameMethod = validEnum.enumClass().getMethod("getDisplayName");
		} catch (NoSuchMethodException e) {
			log.debug("[EnumValidator] '{}' enum getDisplayName() 메서드가 없어 무시합니다.",
				validEnum.enumClass().getSimpleName());
		}

		for (Object constant : enumConstants) {
			Enum<?> enumValue = (Enum<?>)constant;
			if (enumValue.name().equalsIgnoreCase(value)) {
				return true;
			}
			if (getDisplayNameMethod != null) {
				try {
					Object displayName = getDisplayNameMethod.invoke(constant);
					if (value.equalsIgnoreCase(displayName.toString())) {
						return true;
					}
				} catch (Exception e) {
					log.warn("[EnumValidator] '{}' enum getDisplayName() 호출 중 예외 발생",
						validEnum.enumClass().getSimpleName(), e);
				}
			}
		}

		return false;
	}
}

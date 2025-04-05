package com.backend.global.validator;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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

		return Arrays.stream(enumConstants)
			.map(enumConst -> ((Enum<?>)enumConst).name())
			.anyMatch(enumName -> enumName.equalsIgnoreCase(value));
	}
}

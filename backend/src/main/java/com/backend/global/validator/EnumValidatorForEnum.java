package com.backend.global.validator;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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

		Object[] enumConstants = validEnum.enumClass().getEnumConstants();
		if (enumConstants == null) {
			return false;
		}

		return Arrays.asList(enumConstants).contains(value);
	}
}

package com.backend.global.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCache {

	String prefix() default "global";

	String key() default "";

	String id() default "";

	long ttl() default 5;

	TimeUnit ttlUnit() default TimeUnit.MINUTES;

	boolean useMember() default false;

}

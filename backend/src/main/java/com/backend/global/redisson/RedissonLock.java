package com.backend.global.redisson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

	String key(); // Lock의 key

	long waitTime() default 5000L; // Lock 획득 시도 시간

	long leaseTime() default 2000L; // Lock 점유하는 시간
}

package com.backend.global.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomOAuth2UserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long id() default 1L;

    String email() default "test@example.com";

    String role() default "ROLE_USER";
}

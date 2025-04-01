package com.backend.global.auth;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.backend.global.auth.oauth2.CustomOAuth2User;

public class WithMockCustomOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CustomOAuth2User principal = new CustomOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority(withMockCustomUser.role())),
            Map.of("email", withMockCustomUser.email()), // attributes 예시
            "email",
            withMockCustomUser.id(),
            withMockCustomUser.email()
        );

        context.setAuthentication(new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities()
        ));

        return context;
    }
}

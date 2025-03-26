package com.backend.global.auth.oauth2.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Value("${spring.security.oauth2.authorized-redirect-uri}")
	private String redirectUri;

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		log.error("OAuth2 Login Failure: {}", exception.getMessage());

		// 실패 상태와 함께 리다이렉트
		String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("status", "fail")
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}

package com.backend.global.auth.oauth2.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.backend.global.response.GenericResponse;
import com.backend.global.util.AuthResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception
	) throws IOException {

		log.error("OAuth2 Login Failure: {}", exception.getMessage());

		String errorCode = "OAUTH2_UNKNOWN";
		String errorMessage = "OAuth2 로그인 중 알 수 없는 오류가 발생했습니다.";

		if (exception instanceof OAuth2AuthenticationException oauth2Ex) {
			errorCode = oauth2Ex.getError().getErrorCode();
			errorMessage = oauth2Ex.getError().getDescription();
		}

		GenericResponse<?> errorResponse = GenericResponse.fail(HttpStatus.UNAUTHORIZED.value(), errorCode, errorMessage);
		AuthResponseUtil.fail(response, errorResponse, HttpStatus.UNAUTHORIZED.value(), objectMapper);
	}
}

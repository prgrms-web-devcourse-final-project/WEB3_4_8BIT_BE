package com.backend.global.auth.oauth2.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 로그인 실패 시 호출되는 핸들러.
 *
 * <p>예외가 발생하면 설정된 리다이렉트 URI로 다음과 같은 쿼리 파라미터를 포함해 리다이렉트됩니다.</p>
 *
 * <pre>{@code
 * 예시 리다이렉트 URL (인코딩 포함):
 * https://your-frontend.com/oauth/callback?status=fail&code=3001&error=%EC%9D%B4%EB%AF%B8%20%EB%8B%A4%EB%A5%B8%20%EC%86%8C%EC%85%9C%20%EA%B3%84%EC%A0%95%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EC%9E%85%EB%90%9C%20%EA%B3%84%EC%A0%95%EC%9E%85%EB%8B%88%EB%8B%A4.
 *
 * 디코딩 후 프론트엔드에서 사용할 수 있는 형태:
 * {
 *   "status": "fail",
 *   "code": "3001",
 *   "error": "이미 다른 소셜 계정으로 가입된 계정입니다."
 * }
 * }</pre>
 */
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

		String errorCode = "OAUTH2_UNKNOWN";
		String errorMessage = "OAuth2 로그인 중 알 수 없는 오류가 발생했습니다.";

		if (exception instanceof OAuth2AuthenticationException oauth2Ex) {
			errorCode = oauth2Ex.getError().getErrorCode();
			errorMessage = oauth2Ex.getError().getDescription();
		}

		String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("status", "fail")
			.queryParam("code", errorCode)
			.queryParam("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8))
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}

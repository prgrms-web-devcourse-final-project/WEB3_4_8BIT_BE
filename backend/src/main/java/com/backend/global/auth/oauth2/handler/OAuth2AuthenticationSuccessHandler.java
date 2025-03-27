package com.backend.global.auth.oauth2.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.backend.global.auth.jwt.JwtTokenProvider;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final CookieUtil cookieUtil;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	@Value("${spring.security.oauth2.authorized-redirect-uri}")
	private String redirectUri;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		Long userId = oAuth2User.getId();

		log.debug("OAuth2 사용자 정보 - userId: {}, email: {}", userId, oAuth2User.getEmail());

		// Access Token 생성 및 쿠키에 설정 (30분)
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		ResponseCookie cookie = cookieUtil.createAccessTokenCookie(accessToken);

		String refreshTokenKey = "RT:" + userId;
		String existingRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

		//refreshtoken 발급 및 저장
		if (existingRefreshToken == null || jwtTokenProvider.isRefreshTokenExpired(existingRefreshToken)) {
			String refreshToken = jwtTokenProvider.createRefreshToken();
			redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
			log.debug("새로운 Refresh Token 발급 및 저장 완료");
		} else {
			log.debug("기존 Refresh Token 유지");
		}

		// 쿠키 추가
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		log.debug("OAuth2 Login Success: userId-{}", userId);

		// 성공 상태와 함께 리다이렉트
		String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("status", "success")
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}

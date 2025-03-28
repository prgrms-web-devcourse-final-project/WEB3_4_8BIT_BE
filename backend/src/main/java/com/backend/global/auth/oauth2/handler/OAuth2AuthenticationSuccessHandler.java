package com.backend.global.auth.oauth2.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.backend.domain.member.entity.Members;
import com.backend.domain.member.exception.MembersErrorCode;
import com.backend.domain.member.exception.MembersException;
import com.backend.domain.member.repository.MembersRepository;
import com.backend.global.auth.jwt.JwtTokenProvider;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.response.GenericResponse;
import com.backend.global.util.AuthResponseUtil;
import com.backend.global.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final MembersRepository membersRepository;
	private final CookieUtil cookieUtil;
	private final ObjectMapper objectMapper;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		Long userId = oAuth2User.getId();

		Members member = membersRepository.findById(userId)
			.orElseThrow(() -> new MembersException(MembersErrorCode.MEMBER_NOT_FOUND));

		log.debug("OAuth2 로그인 성공 - userId: {}, email: {}", userId, oAuth2User.getEmail());

		// AccessToken 생성 및 쿠키 생성
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);

		// RefreshToken Redis 저장
		String refreshTokenKey = "RT:" + userId;
		String existingRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

		if (existingRefreshToken == null || jwtTokenProvider.isRefreshTokenExpired(existingRefreshToken)) {
			String refreshToken = jwtTokenProvider.createRefreshToken();
			redisTemplate.opsForValue()
				.set(refreshTokenKey, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
			log.debug("새로운 RefreshToken 저장 완료");
		} else {
			log.debug("기존 RefreshToken 유지됨");
		}

		Map<String, Object> loginResponse;

		// isAddInfo에따라서 분기 처리
		if (member.getIsAddInfo()) {
			loginResponse = Map.of(
				"id", member.getMemberId(),
				"email", member.getEmail(),
				"nickname", member.getNickname(),
				"profileImg", member.getProfileImg(),
				"isAddInfo", true
			);
		} else {
			loginResponse = Map.of(
				"id", member.getMemberId(),
				"email", member.getEmail(),
				"profileImg", member.getProfileImg(),
				"isAddInfo", false
			);
		}

		// JSON 응답 + accessToken 쿠키 세팅
		GenericResponse<?> successResponse = GenericResponse.of(true, loginResponse, "OAuth2 로그인 성공");
		AuthResponseUtil.success(response, accessTokenCookie, HttpServletResponse.SC_OK, successResponse, objectMapper);
	}
}

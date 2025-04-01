package com.backend.global.auth.oauth2.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.backend.domain.captain.repository.CaptainRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
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
	private final MemberRepository memberRepository;
	private final CaptainRepository captainRepository;
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

		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

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

		Map<String, Object> loginResponse = getLoginResponse(member);

		// JSON 응답 + accessToken 쿠키 세팅
		GenericResponse<Map<String, Object>> successResponse
			= GenericResponse.of(true, loginResponse, "OAuth2 로그인 성공");

		AuthResponseUtil.success(response, accessTokenCookie, HttpServletResponse.SC_OK, successResponse, objectMapper);
	}

	/**
	 * 회원 객체(Member)를 기반으로 로그인 응답 정보를 구성하는 유틸 메서드
	 *
	 * 회원의 추가 정보 입력 여부와 역할(Role)에 따라 반환되는 데이터 분기
	 * - 추가 정보 미입력: 기본 회원 정보만 포함
	 * - 선장(Captain): 선장 전용 정보(선박 번호 등) 포함
	 * - 일반 회원(User): 닉네임 포함한 일반 정보 반환
	 *
	 * @param member 로그인한 회원 객체
	 * @return Map 로그인 응답 데이터 (key-value 형태)
	 * @implSpec
	 * - 추가 정보 미입력 시 isAddInfo는 false
	 * - Captain 인스턴스일 경우 선장 관련 정보 포함
	 * - 그 외 일반 회원 정보 반환
	 */

	private Map<String, Object> getLoginResponse(Member member) {
		Map<String, Object> loginResponse = new HashMap<>();

		if (!member.getIsAddInfo()) {
			// 1. 추가정보 미입력 상태
			loginResponse.put("id", member.getMemberId());
			loginResponse.put("name", member.getName());
			loginResponse.put("profileImg", member.getProfileImg());
			loginResponse.put("role", member.getRole().name());
			loginResponse.put("provider", member.getProvider().name());
			loginResponse.put("isAddInfo", false);

		} else {
			// 2. 선장 여부 확인 (memberId로 Captain 조회)
			Long memberId = member.getMemberId();
			captainRepository.findById(memberId).ifPresentOrElse(captain -> {
				// 선장일 경우 선장 전용 정보 포함
				loginResponse.put("id", memberId);
				loginResponse.put("name", member.getName());
				loginResponse.put("nickname", member.getNickname());
				loginResponse.put("profileImg", member.getProfileImg());
				loginResponse.put("role", member.getRole().name());
				loginResponse.put("provider", member.getProvider().name());
				loginResponse.put("isAddInfo", true);
				loginResponse.put("shipLicenseNumber", captain.getShipLicenseNumber());
				loginResponse.put("shipList", captain.getShipList());
			}, () -> {
				// 일반 회원
				loginResponse.put("id", memberId);
				loginResponse.put("name", member.getName());
				loginResponse.put("nickname", member.getNickname());
				loginResponse.put("profileImg", member.getProfileImg());
				loginResponse.put("role", member.getRole().name());
				loginResponse.put("provider", member.getProvider().name());
				loginResponse.put("isAddInfo", true);
			});
		}

		return loginResponse;
	}
}

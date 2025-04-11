package com.backend.global.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.jwt.JwtTokenProvider;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	private final CookieUtil cookieUtil;

	@Override
	public boolean beforeHandshake(
		@NonNull final ServerHttpRequest request,
		@NonNull final ServerHttpResponse response,
		@NonNull final WebSocketHandler wsHandler,
		@NonNull final Map<String, Object> attributes
	) {
		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest httpRequest = servletRequest.getServletRequest();

			String accessToken = cookieUtil.extractTokenFromCookie(httpRequest);
			log.debug("WebSocket - 쿠키에서 accessToken 추출: {}", accessToken);

			if (accessToken == null) {
				log.warn("WebSocket 연결 시 accessToken 쿠키 없음");
				return false;
			}

			try {
				if (jwtTokenProvider.isBlacklisted(accessToken)) {
					log.warn("WebSocket 연결 시 블랙리스트 토큰");
					return false;
				}

				if (!jwtTokenProvider.validateToken(accessToken)) {
					log.warn("WebSocket 연결 시 유효하지 않은 accessToken");
					return false;
				}

				// TODO 추후에 redis pub/sub 적용하면 이것도 redis로 관리
				Long senderId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);
				MemberResponse.ChatProfile chatProfile = memberService.getChatProfile(senderId);
				attributes.put("senderId", senderId);
				attributes.put("nickname", chatProfile.nickname());
				attributes.put("fileUrl", chatProfile.fileUrl());

				log.info("WebSocket 연결 인증 성공, senderId={}", senderId);
				return true;

			} catch (Exception e) {
				log.error("WebSocket 연결 인증 실패: {}", e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	public void afterHandshake(
		@NonNull final ServerHttpRequest request,
		@NonNull final ServerHttpResponse response,
		@NonNull final WebSocketHandler wsHandler,
		Exception exception) {

	}

}

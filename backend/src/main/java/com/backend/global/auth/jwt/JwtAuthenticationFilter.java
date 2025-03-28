package com.backend.global.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CookieUtil cookieUtil;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/swagger-ui")
			|| path.startsWith("/v3/api-docs")
			|| path.startsWith("/swagger-resources")
			|| path.startsWith("/webjars");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) {

		try {
			// 쿠키에서 JWT 토큰 추출
			String accessToken = cookieUtil.extractTokenFromCookie(request);
			log.debug("JwtAuthenticationFilter - 쿠키에서 accessToken 추출: {}", accessToken);

			if (accessToken != null) {
				// Access Token 블랙리스트 확인
				if (jwtTokenProvider.isBlacklisted(accessToken)) {
					log.warn("JwtAuthenticationFilter - 블랙리스트에 등록된 accessToken");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				try {
					// /auth/refresh 요청이 아닌 경우에만 토큰 유효성 검증
					if (!request.getRequestURI().equals("/auth/refresh")) {
						if (jwtTokenProvider.validateToken(accessToken)) {
							Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
							SecurityContextHolder.getContext().setAuthentication(authentication);
							log.debug("사용자 '{}'의 인증 정보를 security context에 설정함", authentication.getName());
						}
					}
				} catch (JwtAuthenticationException e) {
					// /auth/refresh 요청이 아닌 경우에만 예외 처리
					if (!request.getRequestURI().equals("/auth/refresh")) {
						throw e;
					}
				}
			}

			filterChain.doFilter(request, response);

		} catch (JwtAuthenticationException e) {
			log.warn("JWT 인증 예외 발생: {}", e.getMessage());
			throw e;

		} catch (Exception e) {
			log.warn("사용자 인증 정보를 설정할 수 없음: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
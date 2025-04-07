package com.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.global.auth.jwt.JwtAuthenticationFilter;
import com.backend.global.auth.jwt.JwtTokenProvider;
import com.backend.global.auth.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.backend.global.auth.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.backend.global.auth.oauth2.service.CustomOAuth2UserService;
import com.backend.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsConfig corsConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CookieUtil cookieUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, cookieUtil, redisTemplate);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CORS 설정 활성화 - corsConfigurationSource 빈을 통해 설정
			.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

			.csrf(AbstractHttpConfigurer::disable)

			// H2 콘솔 설정 추가
			.headers(headers ->
				headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

			// 인증 실패 처리 추가
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json; charset=utf-8");
					response.getWriter().write("{\"message\": \"인증되지 않은 사용자입니다.\"}");
				})
			)

			// 세션 설정 - JWT를 사용하므로 세션을 생성하지 않음
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 요청에 대한 권한 설정
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api/v1/fishes/popular").permitAll()

					.requestMatchers(HttpMethod.POST, "/api/v1/fishes/encyclopedias")
					.hasAnyRole("USER", "CAPTAIN", "ADMIN")

					.requestMatchers(
						HttpMethod.GET,
						"/api/v1/fishes/{fishId}/encyclopedias",
						"/api/v1/fishes/{fishId}"
					)
					.hasAnyRole("USER", "CAPTAIN", "ADMIN")

					.anyRequest().authenticated()
			)

			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2AuthenticationSuccessHandler) // 로그인 성공 시 처리할 핸들러
				.failureHandler(oAuth2AuthenticationFailureHandler) // 로그인 실패 시 처리할 핸들러
				.userInfoEndpoint(userinfo -> userinfo
					.userService(customOAuth2UserService))
			)

			// JWT 필터 추가
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * ROLE Prefix 빈 값으로 변경
	 */
	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}
}
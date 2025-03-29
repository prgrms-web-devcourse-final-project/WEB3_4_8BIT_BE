package com.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * SwaggerConfig
 * <p>Swagger 설정 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi api() {
		return GroupedOpenApi.builder()
			.group("apiV1")
			.pathsToMatch("/api/**")
			.build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		// Cookie 기반 인증 스키마 정의
		SecurityScheme cookieAuth = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.COOKIE)
			.name("accessToken"); // 쿠키 이름

		return new OpenAPI()
			.info(new Info()
				.title("미끼미끼 프로젝트")
				.version("1.0.0")
				.description("미끼미끼 API 명세서"))
			.components(new Components()
				.addSecuritySchemes("cookieAuth", cookieAuth))
			.addSecurityItem(new SecurityRequirement().addList("cookieAuth"));
	}
}
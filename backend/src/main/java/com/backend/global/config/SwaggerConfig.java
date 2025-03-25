package com.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * SwaggerConfig
 * <p>Swagger 설정 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "미끼미끼 프로젝트", version = "v1"))
// @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi api() {
		return GroupedOpenApi.builder()
			.group("apiV1")
			.pathsToMatch("/api/**")
			.build();
	}
}
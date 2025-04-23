package com.backend.global.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.connectTimeout(Duration.ofSeconds(3))    //서버 연결 시도 최대 시간
			.readTimeout(Duration.ofSeconds(5))    //데이터 응답 대기 최대 시간
			.build();
	}
}

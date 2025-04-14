package com.backend;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostConstruct
	void started() {
		log.info("서버 타임존 설정 전 확인: {}", ZonedDateTime.now());
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("서버 타임존 설정 후 확인: {}", ZonedDateTime.now());
	}
}

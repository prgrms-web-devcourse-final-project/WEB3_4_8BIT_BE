package com.backend.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * AsyncConfig
 * <p>Async 설정 클래스 입니다.</p>
 *
 * @author KimDongO
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "threadPoolTaskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		//스레드 풀 기본 사이즈 설정
		executor.setCorePoolSize(3);
		//대기열이 가득차면 추가로 사용할 스레드 최대 사이즈 설정
		executor.setMaxPoolSize(10);
		//corePoolSize가 가득 찬 상태에서 대기시킬 작업 개수
		executor.setQueueCapacity(500);
		//스레드 prefix
		executor.setThreadNamePrefix("Executor-");
		executor.initialize();
		return executor;
	}
}
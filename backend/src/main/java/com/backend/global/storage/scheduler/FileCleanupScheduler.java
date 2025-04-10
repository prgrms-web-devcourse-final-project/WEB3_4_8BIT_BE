package com.backend.global.storage.scheduler;

import java.time.Duration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.global.storage.service.StorageCleanupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

	private final StorageCleanupService storageCleanupService;

	/**
	 * 10분마다 실행되며, 10분 이상 업로드되지 않은 파일 정리
	 */
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void deletePendingFiles() {
		try {
			storageCleanupService.deletePendingFiles(Duration.ofMinutes(10));
		} catch (Exception e) {
			log.error("파일 정리 스케줄러 실행 중 오류 발생", e);
		}
	}
}

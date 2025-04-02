package com.backend.global.storage.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.backend.global.storage.repository.StorageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageCleanupServiceImpl implements StorageCleanupService {

	private final StorageRepository storageRepository;

	@Override
	@Transactional
	public void deletePendingFiles(Duration olderThan) {

		LocalDateTime expirationTime = LocalDateTime.now().minus(olderThan);

		int deletedCount = storageRepository.deletePendingFilesBefore(expirationTime);

		log.debug("업로드되지 않은 파일 삭제 완료 - 기준 시각: {}, 삭제된 개수: {}", expirationTime, deletedCount);
	}
}

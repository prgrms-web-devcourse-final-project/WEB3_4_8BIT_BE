package com.backend.global.storage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.global.storage.entity.File;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepository {

	private final StorageJpaRepository storageJpaRepository;

	@Override
	public File save(final File file) {
		return storageJpaRepository.save(file);
	}

	@Override
	public List<File> saveAll(final List<File> fileList) {
		return storageJpaRepository.saveAll(fileList);
	}

	@Override
	public List<File> findAllById(final List<Long> idList) {
		return storageJpaRepository.findAllById(idList);
	}

	@Override
	public int deletePendingFilesBefore(LocalDateTime expirationTime) {
		return storageJpaRepository.deletePendingFilesBefore(expirationTime);
	}
}

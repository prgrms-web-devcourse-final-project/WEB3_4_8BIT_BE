package com.backend.global.storage.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.global.storage.entity.File;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepository {

	private final StorageJpaRepository storageJpaRepository;
	private final StorageQueryRepository storageQueryRepository;

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
	public Long deletePendingFilesBefore(final ZonedDateTime expirationTime) {
		return storageQueryRepository.deletePendingFilesBefore(expirationTime);
	}

	@Override
	public void deleteAll(final List<File> fileList) {
		storageJpaRepository.deleteAll(fileList);
	}
}

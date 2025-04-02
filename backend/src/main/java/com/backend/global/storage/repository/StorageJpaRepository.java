package com.backend.global.storage.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.backend.global.storage.entity.File;

public interface StorageJpaRepository extends JpaRepository<File, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM File f WHERE f.uploaded = false AND f.createdAt < :expirationTime")
	int deletePendingFilesBefore(@Param("expirationTime") LocalDateTime expirationTime);
}

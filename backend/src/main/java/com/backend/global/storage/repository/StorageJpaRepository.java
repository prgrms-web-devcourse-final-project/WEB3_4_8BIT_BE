package com.backend.global.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.global.storage.entity.File;

public interface StorageJpaRepository extends JpaRepository<File, Long> {
}

package com.backend.global.storage.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.global.storage.entity.File;

public interface StorageRepository {

	/**
	 * 이미지 저장 메서드
	 *
	 * @param file presignedURL 요청 데이터 기반으로 만든 이미지 객체
	 * @return {@link File )
	 */
	File save(final File file);

	/**
	 * 이미지 리스트 저장 메서드
	 *
	 * @param fileList 저장할 파일 리스트
	 * @return {@link List<File> )
	 */
	List<File> saveAll(final List<File> fileList);

	/**
	 * 파일 ID로 조회 메서드
	 *
	 * @param idList 조회할 파일 ID 리스트
	 * @return {@link List<File> )
	 */
	List<File> findAllById(final List<Long> idList);

	/**
	 * 업로드되지 않은 파일 중, 지정된 시각 이전에 생성된 파일들을 삭제
	 *
	 * @param expirationTime 삭제 기준 시각 (이 시각보다 이전에 생성된 파일이 삭제 대상)
	 * @return {@link Integer} 삭제된 파일 개수
	 */
	int deletePendingFilesBefore(final LocalDateTime expirationTime);
}

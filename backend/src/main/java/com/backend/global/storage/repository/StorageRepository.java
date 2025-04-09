package com.backend.global.storage.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
	 * @return {@link Long} 삭제된 파일 개수
	 */
	Long deletePendingFilesBefore(final ZonedDateTime expirationTime);

	/**
	 * 주어진 파일 리스트 삭제
	 *
	 * @param fileList 삭제할 {@link File} 엔티티 리스트
	 */
	void deleteAll(final List<File> fileList);

	/**
	 * 파일 ID를 기반으로 해당 파일 정보를 조회합니다.
	 *
	 * <p>저장소에서 해당 ID에 매핑되는 {@link File} 엔티티를 조회하며,
	 * 존재하지 않을 경우 빈 {@link Optional}을 반환합니다.</p>
	 *
	 * @param fileId 조회할 파일의 ID
	 * @return 주어진 ID에 해당하는 {@link File}이 존재하면 {@link Optional}로 반환하고,
	 *         없으면 {@link Optional#empty()}를 반환합니다.
	 */
	Optional<File> findById(final Long fileId);
}

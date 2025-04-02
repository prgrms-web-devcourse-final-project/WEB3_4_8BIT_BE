package com.backend.global.storage.service;

import java.util.List;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;

public interface StorageService {

	/**
	 * 업로드할 파일의 presigned URL 생성 메서드
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param uploadFileList 파일 정보 리스트
	 * @return {@link List<FileUploadResponse>}
	 * @implSpec 도메인과 파일 정보를 받아서 PresignedURL을 생성 후 리턴
	 * @author vdvhk12
	 */
	List<FileUploadResponse> generateUploadUrls(
		final Long memberId,
		final String domain,
		final List<FileUploadRequest.UploadFile> uploadFileList
	);

	/**
	 * 업로드 완료 요청에 따라 파일 상태를 uploaded = true 로 변경
	 *
	 * @param memberId 업로드 완료를 요청한 사용자 ID (파일 소유자 검증에 사용)
	 * @param fileIdList 업로드 완료 처리할 파일 ID 리스트
	 * @return {@link List<Long>}
	 * @implSpec fileId 목록에 해당하는 파일을 조회 후 소유자 및 업로드 여부 검증 후 상태 변경
	 * @author vdvhk12
	 */
	List<Long> confirmFileUpload(final Long memberId, final List<Long> fileIdList);

	/**
	 * 주어진 파일 ID 리스트에 해당하는 파일을 삭제
	 * S3 저장소와 DB 모두 삭제
	 *
	 * @param fileIdList 삭제할 파일 ID 목록
	 */
	void deleteFilesByIdList(Long memberId, List<Long> fileIdList);
}

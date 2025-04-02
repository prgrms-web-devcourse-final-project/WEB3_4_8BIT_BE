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
}

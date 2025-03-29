package com.backend.global.storage.service;

import java.util.List;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;

public interface StorageService {
	/**
	 * 하나의 파일에 대한 presigned URL 생성 메서드
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param file 파일 정보
	 * @return {@link FileUploadResponse}
	 * @implSpec 도메인, 파일명, 파일 크기, 파일 타입을 받아서 검증 후, PresignedURL을 생성하고 리턴
	 * @author vdvhk12
	 */
	FileUploadResponse generateUploadUrl(String domain, FileUploadRequest.File file);

	/**
	 * 여러 개의 파일에 대한 presigned URL 생성 메서드
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param files 파일 정보 리스트
	 * @return {@link List<FileUploadResponse>}
	 * @implSpec 도메인과 파일 정보를 받아서 PresignedURL을 생성 후 리턴
	 * @author vdvhk12
	 */
	List<FileUploadResponse> generateUploadUrls(String domain, List<FileUploadRequest.File> files);
}

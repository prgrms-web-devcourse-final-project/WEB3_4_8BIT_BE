package com.backend.global.storage.service;

import java.util.List;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;

public interface StorageService {
	FileUploadResponse generateUploadUrl(String domain, String fileName, Long fileSize, String contentType);
	List<FileUploadResponse> generateUploadUrls(String domain, List<FileUploadRequest> files);
}

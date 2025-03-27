package com.backend.global.storage.service;

import java.util.List;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;

public interface StorageService {
	FileUploadResponse generateUploadUrl(String domain, FileUploadRequest.File file);
	List<FileUploadResponse> generateUploadUrls(String domain, List<FileUploadRequest.File> files);
}

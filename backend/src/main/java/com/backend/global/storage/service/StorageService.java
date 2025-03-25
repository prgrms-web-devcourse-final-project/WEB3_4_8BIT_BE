package com.backend.global.storage.service;

import java.util.List;

import com.backend.global.storage.dto.request.FileUploadRequest;

public interface StorageService {
	String generateUploadUrl(String fileName, Long fileSize, String contentType);
	List<String> generateUploadUrls(List<FileUploadRequest> files);
}

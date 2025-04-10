package com.backend.global.storage.dto.response;

public record FileUploadResponse(Long fileId, String fileName, String presignedUrl) {

	public static FileUploadResponse of(Long fileId, String fileName, String presignedUrl) {
		return new FileUploadResponse(fileId, fileName, presignedUrl);
	}
}

package com.backend.global.storage.dto.response;

public record FileUploadResponse(String fileName, String presignedUrl) {

	public static FileUploadResponse of(String fileName, String presignedUrl) {
		return new FileUploadResponse(fileName, presignedUrl);
	}
}

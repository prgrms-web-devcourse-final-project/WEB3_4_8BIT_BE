package com.backend.global.storage.converter;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.entity.File;

import lombok.Builder;

@Builder
public class StorageConverter {

	public static File from(
		Long memberId,
		String domain,
		FileUploadRequest.UploadFile uploadFile,
		String filename,
		String accessUrl
	) {
		return File.builder()
			.fileName(filename)
			.originalFileName(uploadFile.originalFileName())
			.contentType(uploadFile.contentType())
			.fileSize(uploadFile.fileSize())
			.url(accessUrl)
			.domain(domain)
			.createdById(memberId)
			.uploaded(false)
			.build();
	}
}

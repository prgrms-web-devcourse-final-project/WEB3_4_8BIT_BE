package com.backend.global.storage.service.dto;

import com.backend.global.storage.entity.File;

/**
 * 파일 객체랑 응답 데이터를 담은 DTO
 *
 * @param file			DB에 저장할 파일 엔티티
 * @param fileName		S3에 저장될 파일 이름
 * @param presignedUrl	프론트에 전달할 S3 presigned URL
 */
public record FileToUpload (
	File file,
	String fileName,
	String presignedUrl
) {
}

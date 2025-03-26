package com.backend.global.storage.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@Primary
public class S3StorageService implements StorageService {

	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB 제한
	private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
		"image/png",
		"image/jpeg",
		"image/jpg"
	);

	private final S3Presigner s3Presigner;
	private final String bucketName;

	public S3StorageService(S3Presigner s3Presigner, @Value("${aws.s3.bucket-name}") String bucketName) {
		this.s3Presigner = s3Presigner;
		this.bucketName = bucketName;
	}

	/**
	 * 여러 개의 파일에 대한 Presigned URL 생성
	 */
	@Override
	public List<FileUploadResponse> generateUploadUrls(String domain, List<FileUploadRequest> files) {
		return files.stream()
			.map(file -> generateUploadUrl(domain, file.fileName(), file.fileSize(), file.contentType()))
			.toList();
	}

	@Override
	public FileUploadResponse generateUploadUrl(String domain, String fileName, Long fileSize, String contentType) {
		// 파일 크기, MIME 타입 검증
		validateFile(fileSize, contentType);

		// 파일명 생성
		String newFileName = generateFileName(domain, fileName);

		// Presigned URL 생성 요청
		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(newFileName)
			.contentLength(fileSize)
			.contentType(contentType)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(b -> b
			.signatureDuration(Duration.ofMinutes(10)) // Presigned URL 유효기간
			.putObjectRequest(objectRequest)
		);

		return FileUploadResponse.of(newFileName, presignedRequest.url().toString());
	}

	/**
	 * 파일 검증 (크기 및 MIME 타입)
	 */
	private void validateFile(long fileSize, String contentType) {
		// TODO 커스텀 예외 처리 추후에 수정
		if (fileSize <= 0 || fileSize > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 크기가 허용된 최대 크기(10MB)를 초과했습니다.");
		}

		if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (허용된 형식: PNG, JPEG, JPG)");
		}
	}

	/**
	 * 파일명 생성
	 */
	private String generateFileName(String domain, String originalFileName) {
		return domain + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFileName;
	}
}

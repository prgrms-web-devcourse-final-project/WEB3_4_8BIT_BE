package com.backend.global.storage.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;
import com.backend.global.storage.exception.StorageErrorCode;
import com.backend.global.storage.exception.StorageException;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@Primary
public class S3StorageService implements StorageService {

	private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
		"image/png",
		"image/jpeg",
		"image/jpg"
	);

	private final S3Presigner s3Presigner;
	private final String bucketName;
	private final DataSize maxFileSize;

	public S3StorageService(
		S3Presigner s3Presigner,
		@Value("${aws.s3.bucket-name}") String bucketName,
		@Value("${aws.s3.max-file-size}") DataSize maxFileSize
	) {
		this.s3Presigner = s3Presigner;
		this.bucketName = bucketName;
		this.maxFileSize = maxFileSize;
	}

	/**
	 * 여러 개의 파일에 대한 presigned URL 생성 메서드
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param files 파일 정보 리스트
	 * @return {@link List<FileUploadResponse>}
	 * @implSpec 도메인과 파일 정보를 받아서 PresignedURL을 생성 후 리턴
	 * @author vdvhk12
	 */
	@Override
	public List<FileUploadResponse> generateUploadUrls(String domain, List<FileUploadRequest> files) {
		return files.stream()
			.map(file -> generateUploadUrl(domain, file.fileName(), file.fileSize(), file.contentType()))
			.toList();
	}

	/**
	 * 하나의 파일에 대한 presigned URL 생성 메서드
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param fileName 파일 이름
	 * @param fileSize 파일 크기
	 * @param contentType 파일 타입
	 * @return {@link FileUploadResponse}
	 * @implSpec 도메인, 파일명, 파일 크기, 파일 타입을 받아서 검증 후, PresignedURL을 생성하고 리턴
	 * @author vdvhk12
	 */
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
	 * 파일 검증(크기 및 MIME 타입)
	 *
	 * @param fileSize 파일 크기
	 * @param contentType 파일 타입
	 * @implSpec 파일 크기와 파일 타입을 검증 후 예외 처리
	 * @author vdvhk12
	 */
	private void validateFile(long fileSize, String contentType) {
		// TODO 지금은 일단 커스텀 예외로 잡고, 추후에 Validation 으로 파일 크기를 검증
		if (fileSize > maxFileSize.toBytes()) {
			throw new StorageException(StorageErrorCode.FILE_SIZE_EXCEEDED);
		}

		if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE);
		}
	}

	/**
	 * 파일명 중복 방지를 위한 파일명 생성
	 *
	 * @param domain 파일이 사용되는 도메인
	 * @param originalFileName 파일 이름
	 * @return String
	 * @implSpec 도메인, 파일명을 받아서 도메인과 랜덤 값을 추가해 파일명 생성 후 리턴
	 * @author vdvhk12
	 */
	private String generateFileName(String domain, String originalFileName) {
		return domain + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFileName;
	}
}

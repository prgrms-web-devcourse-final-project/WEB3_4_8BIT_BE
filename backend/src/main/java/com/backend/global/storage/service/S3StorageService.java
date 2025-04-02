package com.backend.global.storage.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.global.storage.converter.StorageConverter;
import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.exception.StorageErrorCode;
import com.backend.global.storage.exception.StorageException;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.dto.FileToUpload;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Slf4j
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
	private final StorageRepository storageRepository;

	public S3StorageService(
		S3Presigner s3Presigner,
		@Value("${aws.s3.bucket-name}") String bucketName,
		StorageRepository storageRepository
	) {
		this.s3Presigner = s3Presigner;
		this.storageRepository = storageRepository;
		this.bucketName = bucketName;
	}

	@Override
	@Transactional
	public List<FileUploadResponse> generateUploadUrls(
		final Long memberId,
		final String domain,
		final List<FileUploadRequest.UploadFile> uploadFileList
	) {
		// 1. presigned URL과 DB 저장할 File 엔티티를 함께 보관할 임시 DTO 리스트 생성
		List<FileToUpload> filesToUpload = new ArrayList<>();

		// 2. 업로드 요청으로 받은 파일 리스트를 순회하며 presigned URL 및 File 엔티티 생성
		for (FileUploadRequest.UploadFile uploadFile : uploadFileList) {
			// 2-1. 파일 타입 유효성 검사 (image/png, image/jpeg, image/jpg만 허용)
			validateFile(uploadFile.contentType());

			// 2-2. S3에 저장할 파일 이름 생성 (도메인/UUID-원본이름)
			String uploadFileName = generateFileName(domain, uploadFile.originalFileName());

			// 2-3. S3 presigned URL 생성을 위한 요청 객체 구성
			PutObjectRequest objectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(uploadFileName)
				.contentLength(uploadFile.fileSize())
				.contentType(uploadFile.contentType())
				.build();

			// 2-4. presigned URL 생성 (유효기간 10분)
			PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(b -> b
				.signatureDuration(Duration.ofMinutes(10))
				.putObjectRequest(objectRequest)
			);

			// 2-5. DB에 저장할 File 엔티티 생성
			File file = StorageConverter.from(memberId, domain, uploadFile, uploadFileName);

			// 2-6. presigned URL, 파일 이름, File 엔티티를 함께 묶어서 리스트에 저장
			filesToUpload.add(new FileToUpload(file, uploadFileName, presignedRequest.url().toString()));
		}

		// 3. File 엔티티 리스트만 추출해서 DB에 일괄 저장
		List<File> fileList = filesToUpload.stream().map(FileToUpload::file).toList();
		List<File> savedFileList = storageRepository.saveAll(fileList);

		// 4. 저장된 파일 엔티티와 presigned URL 정보를 매핑하여 응답 DTO 생성
		List<FileUploadResponse> response = new ArrayList<>();
		for (int i = 0; i < savedFileList.size(); i++) {
			File savedFile = savedFileList.get(i);
			FileToUpload fileToUpload = filesToUpload.get(i);

			// 4-1. 파일 ID, S3 파일 이름, presigned URL을 응답 DTO에 추가
			response.add(new FileUploadResponse(savedFile.getFileId(), fileToUpload.fileName(), fileToUpload.presignedUrl()));
		}

		// 5. 응답 로그 출력
		log.debug("Presigned URL 리스트 생성 완료 (총 {}개)", response.size());

		// 6. 클라이언트에 presigned URL 리스트 응답
		return response;
	}

	/**
	 * 파일 검증(MIME 타입)
	 *
	 * @param contentType 파일 타입
	 * @author vdvhk12
	 */
	private void validateFile(final String contentType) {
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
	 * @author vdvhk12
	 */
	private String generateFileName(final String domain, final String originalFileName) {
		return domain + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFileName;
	}
}

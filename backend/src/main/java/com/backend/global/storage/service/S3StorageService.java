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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
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
	private final S3Client s3Client;
	private final String bucketName;
	private final StorageRepository storageRepository;

	public S3StorageService(
		S3Presigner s3Presigner,
		S3Client s3Client,
		@Value("${aws.s3.bucket-name}") String bucketName,
		StorageRepository storageRepository
	) {
		this.s3Presigner = s3Presigner;
		this.s3Client = s3Client;
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

	@Override
	@Transactional
	public List<Long> confirmFileUpload(Long memberId, List<Long> fileIdList) {

		// 1. 조회 후 파일 존재 검증
		List<File> fileList = storageRepository.findAllById(fileIdList);
		validateFileIdsExist(fileList, fileIdList);

		// 2. 업로드 상태 중복 처리 방지
		validateNotAlreadyUploaded(fileList);

		// 3. 작성자 일치 검증
		validateOwner(fileList, memberId);

		// 4. 실제 업로드 검증 (필요시 추가)
		// validateUploadedToS3(fileList);

		// 5. uploaded 상태 변경
		for (File file : fileList) {
			file.completeUpload();
		}

		log.debug("업로드 완료 처리된 파일 수: {}", fileList.size());

		return fileList.stream().map(File::getFileId).toList();
	}

	/**
	 * 파일 타입(MIME type) 검증
	 *
	 * @param contentType 업로드할 파일의 MIME 타입
	 * @throws StorageException 허용되지 않은 파일 형식인 경우 예외 발생
	 */
	private void validateFile(final String contentType) {
		if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE);
		}
	}

	/**
	 * 파일명 중복 방지를 위한 고유 파일명 생성
	 *
	 * @param domain 파일이 사용되는 도메인 (예: profile, post 등)
	 * @param originalFileName 클라이언트가 업로드한 원본 파일명
	 * @return S3에 저장할 고유 파일 경로 (도메인/UUID-파일명 형태)
	 */
	private String generateFileName(final String domain, final String originalFileName) {
		return domain + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + originalFileName;
	}

	/**
	 * 파일 ID 유효성 검증
	 *
	 * @param fileList   DB에서 조회된 파일 리스트
	 * @param fileIdList 요청받은 파일 ID 리스트
	 * @throws StorageException 파일 개수가 일치하지 않으면 예외 발생
	 */
	private void validateFileIdsExist(List<File> fileList, List<Long> fileIdList) {
		if (fileList.size() != fileIdList.size()) {
			throw new StorageException(StorageErrorCode.FILE_NOT_FOUND);
		}
	}

	/**
	 * 이미 업로드 처리된 파일이 있는지 검증
	 *
	 * @param fileList DB에서 조회된 파일 리스트
	 * @throws StorageException uploaded = true 인 파일이 하나라도 있으면 예외 발생
	 */
	private void validateNotAlreadyUploaded(List<File> fileList) {
		boolean anyUploaded = fileList.stream().anyMatch(File::getUploaded);
		if (anyUploaded) {
			throw new StorageException(StorageErrorCode.ALREADY_UPLOADED_FILE);
		}
	}

	/**
	 * 파일 작성자(memberId)와 요청자가 일치하는지 검증
	 *
	 * @param fileList DB에서 조회된 파일 리스트
	 * @param memberId 현재 요청한 사용자 ID
	 * @throws StorageException 소유자가 아닌 파일이 하나라도 있으면 예외 발생
	 */
	private void validateOwner(List<File> fileList, Long memberId) {
		for (File file : fileList) {
			if (!file.getCreatedById().equals(memberId)) {
				throw new StorageException(StorageErrorCode.NO_FILE_ACCESS);
			}
		}
	}

	/**
	 * S3에 실제로 파일이 존재하는지 검증
	 *
	 * @param fileList DB에서 조회된 파일 리스트
	 * @throws StorageException S3에 파일이 존재하지 않으면 예외 발생
	 */
	private void validateUploadedToS3(List<File> fileList) {
		for (File file : fileList) {
			if (!isFileUploadedToS3(file.getFileName())) {
				throw new StorageException(StorageErrorCode.S3_FILE_NOT_FOUND);
			}
		}
	}

	/**
	 * S3에 해당 파일이 실제 존재하는지 확인
	 *
	 * @param key S3 버킷 내의 파일 키 (파일 경로)
	 * @return 존재하면 true, 존재하지 않으면 false
	 */
	private boolean isFileUploadedToS3(String key) {
		try {
			s3Client.headObject(b -> b.bucket(bucketName).key(key));
			return true;
		} catch (S3Exception e) {
			return false;
		}
	}
}

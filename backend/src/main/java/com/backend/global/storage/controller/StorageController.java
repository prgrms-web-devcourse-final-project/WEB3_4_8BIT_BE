package com.backend.global.storage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;
import com.backend.global.storage.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage/presigned-urls")
@Tag(name = "파일 업로드 API")
public class StorageController {

	private final StorageService storageService;

	@PostMapping
	@Operation(summary = "Presigned URL 생성", description = "파일 업로드를 위한 presigned URL 리스트를 생성하는 API")
	public ResponseEntity<GenericResponse<List<FileUploadResponse>>> getPresignedUrls(
		@RequestBody @Valid final FileUploadRequest.Request requestDto,
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		List<FileUploadResponse> response = storageService.generateUploadUrls(
			user.getId(),
			requestDto.domain(),
			requestDto.uploadFileList()
		);
		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@PostMapping("/complete")
	@Operation(summary = "파일 업로드 완료 처리", description = "S3에 업로드된 presigned 파일들의 업로드 상태를 완료 처리하는 API")
	public ResponseEntity<GenericResponse<List<Long>>> completeUpload(
		@RequestBody @Valid final FileUploadRequest.Complete request,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {
		List<Long> confirmedFileIdList = storageService.confirmFileUpload(user.getId(), request.fileIdList());
		return ResponseEntity.ok(GenericResponse.of(true, confirmedFileIdList));
	}
}

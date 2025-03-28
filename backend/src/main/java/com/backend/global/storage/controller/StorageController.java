package com.backend.global.storage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.global.response.GenericResponse;
import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.dto.response.FileUploadResponse;
import com.backend.global.storage.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

	private final StorageService storageService;

	@PostMapping("/presigned-urls")
	@Operation(summary = "Presigned URL 생성", description = "파일 업로드를 위한 presigned URL 리스트를 생성합니다.")
	public ResponseEntity<GenericResponse<List<FileUploadResponse>>> getPresignedUrls(
		@RequestBody @Valid FileUploadRequest.Request request
	) {
		List<FileUploadResponse> response = storageService.generateUploadUrls(request.domain(), request.files());
		return ResponseEntity.ok(GenericResponse.of(true, response));
	}
}

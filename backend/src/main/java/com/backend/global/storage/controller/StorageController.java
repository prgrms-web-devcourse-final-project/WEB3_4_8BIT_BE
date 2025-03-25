package com.backend.global.storage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.global.response.GenericResponse;
import com.backend.global.storage.dto.request.FileUploadRequest;
import com.backend.global.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

	private final StorageService storageService;

	@PostMapping("/presigned-urls")
	public ResponseEntity<GenericResponse<?>> getPresignedUrls(@RequestBody List<FileUploadRequest> files) {
		List<String> presignedUrls = storageService.generateUploadUrls(files);
		return ResponseEntity.ok(GenericResponse.ok(presignedUrls));
	}
}

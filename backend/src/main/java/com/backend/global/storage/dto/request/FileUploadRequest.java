package com.backend.global.storage.dto.request;

// TODO Validation 처리 추후에 리팩토링 하면서 진행
public record FileUploadRequest (String fileName, Long fileSize, String contentType) {
}

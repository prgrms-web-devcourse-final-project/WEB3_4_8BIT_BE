package com.backend.global.storage.dto.request;

public record FileUploadRequest (String fileName, Long fileSize, String contentType) {
}

package com.backend.global.storage.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FileUploadRequest {

	@Schema(description = "업로드할 단일 파일 정보")
	public record UploadFile(
		@NotBlank(message = "파일명은 필수 항목입니다.")
		@Schema(description = "파일명", example = "IMG_3687.jpeg")
		String originalFileName,

		@NotNull(message = "파일 크기는 필수 항목입니다.")
		@Min(value = 1, message = "파일 크기는 1바이트 이상이어야 합니다.")
		@Max(value = 10_485_760, message = "파일 크기는 10MB 이하만 가능합니다.")
		@Schema(description = "파일 크기", example = "994527")
		Long fileSize,

		@NotBlank(message = "파일 타입은 필수 항목입니다.")
		@Schema(description = "파일 타입", example = "image/jpeg")
		String contentType
	) {}

	@Schema(description = "Presigned URL 요청 DTO")
	public record Request (
		@NotBlank(message = "도메인은 필수 항목입니다.")
		@Schema(description = "도메인명", example = "profile")
		String domain,

		@NotEmpty
		@Size(max = 10, message = "파일은 최대 10개까지 업로드할 수 있습니다.")
		@Valid
		List<UploadFile> uploadFileList
	) {}

	@Schema(description = "파일 업로드 완료 요청 DTO")
	public record Complete(
		@NotEmpty(message = "파일 ID 리스트는 필수 항목입니다.")
		@Schema(description = "업로드 완료된 파일의 ID 리스트", example = "[101, 102, 103]")
		List<Long> fileIdList
	) {}
}

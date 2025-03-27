package com.backend.domain.fishencyclopedia.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediasRequest;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediasService;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fish/encyclopedias")
@RequiredArgsConstructor
@Tag(name = "물고기 도감 API")
public class FishEncyclopediasController {

	private final FishEncyclopediasService fishEncyclopediasService;

	@Operation(summary = "물고기 도감 추가하기", description = "물고기 도감 추가시 사용하는 API")
	@PostMapping
	public ResponseEntity<GenericResponse<Void>> save(
		@RequestBody @Valid FishEncyclopediasRequest.Create create
	) {

		//TODO 추후 수정 예정
		// fishEncyclopediasService.save(create, )

		Long fishEncyclopediaId = 1L;

		return ResponseEntity
			.created(URI.create(fishEncyclopediaId.toString()))
			.body(GenericResponse.ok());
	}
}
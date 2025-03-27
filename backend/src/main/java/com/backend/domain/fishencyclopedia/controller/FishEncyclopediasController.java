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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fish/encyclopedias")
@RequiredArgsConstructor
public class FishEncyclopediasController {

	private final FishEncyclopediasService fishEncyclopediasService;

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
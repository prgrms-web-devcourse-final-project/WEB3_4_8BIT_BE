package com.backend.domain.shipfishingpost.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.service.ShipFishingPostService;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "선상 낚시 게시글 API")
@RestController
@RequestMapping("/api/v1/ship-post")
@RequiredArgsConstructor
public class ShipFishingPostController {

	private final ShipFishingPostService shipFishingPostService;

	@PostMapping
	@Operation(summary = "선상 낚시 게시글 생성", description = "유저가 새로운 선상 낚시 게시글을 생성할 때 사용하는 API")
	public ResponseEntity<GenericResponse<Void>> createShipFishPost(
		@RequestBody @Valid final ShipFishingPostRequest.Create requestDto
	) {

		// Todo : UserDetails

		Long shipFishPostsId = shipFishingPostService.save(requestDto);

		return ResponseEntity.created(URI.create(shipFishPostsId.toString())).body(GenericResponse.of(true));
	}

	@GetMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 상세 조회", description = "유저가 선상 낚시 게시글을 상세 조회할 때 사용하는 API")
	@Parameter(name = "id", description = "조회할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<ShipFishingPostResponse.Detail>> getShipFishPost(
		@PathVariable("id") final Long shipFishPostsId
	) {

		ShipFishingPostResponse.Detail response = shipFishingPostService.getShipFishingPost(shipFishPostsId);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}
}
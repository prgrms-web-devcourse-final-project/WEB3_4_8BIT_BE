package com.backend.domain.shipfishposts.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.service.ShipFishPostsService;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ship-post")
@RequiredArgsConstructor
public class ShipFishPostsController {

	private final ShipFishPostsService shipFishPostsService;

	@Tag(name = "선상낚시 게시글 > 게시글 생성 API 명세")
	@Operation(
		summary = "선상 낚시 게시글 생성",
		description = "유저가 새로운 선상 낚시 게시글을 생성할 때 사용하는 API"
	)
	@PostMapping
	public ResponseEntity<GenericResponse<Void>> createShipFishPost(
		@RequestBody @Valid ShipFishPostsRequest.Create requestDto) {

		// Todo : UserDetails

		Long shipFishPostsId = shipFishPostsService.save(requestDto);

		return ResponseEntity.created(URI.create(shipFishPostsId.toString())).build();
	}

}

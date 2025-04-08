package com.backend.domain.shipfishingpost.controller;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.service.ShipFishingPostService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "선상 낚시 게시글 API")
@RestController
@RequestMapping("/api/v1/ship-fishing-posts")
@RequiredArgsConstructor
public class ShipFishingPostController {

	private final ShipFishingPostService shipFishingPostService;

	@PostMapping
	@Operation(summary = "선상 낚시 게시글 생성", description = "유저가 새로운 선상 낚시 게시글을 생성할 때 사용하는 API")
	public ResponseEntity<GenericResponse<Void>> createShipFishingPost(
		@RequestBody @Valid final ShipFishingPostRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long shipFishingPostId = shipFishingPostService.createShipFishingPost(requestDto, user.getId());

		return ResponseEntity.created(URI.create(shipFishingPostId.toString())).body(GenericResponse.of(true));
	}

	@GetMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 상세 조회", description = "유저가 선상 낚시 게시글을 상세 조회할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "조회할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<ShipFishingPostResponse.DetailWithFileUrlAndFishName>> getShipFishingPost(
		@PathVariable("id") final Long shipFishPostsId
	) {

		ShipFishingPostResponse.DetailWithFileUrlAndFishName response = shipFishingPostService
			.getShipFishingPostAll(shipFishPostsId);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping
	@Operation(summary = "선상 낚시 게시글 검색 및 조회", description = "유저가 선상 낚시 게시글을 조회할 때 사용하는 API")
	public ResponseEntity<GenericResponse<ScrollResponse<ShipFishingPostResponse.DetailScroll>>> getShipFishingPostList(
		@ParameterObject @ModelAttribute final ShipFishingPostRequest.Search requestDto,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto
	) {

		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostService
			.getShipFishingPostScroll(requestDto, cursorRequestDto);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 삭제", description = "유저가 선상 낚시 게시글을 삭제할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "삭제할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<Void>> deleteShipFishingPost(
		@PathVariable("id") final Long shipFishPostsId,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		shipFishingPostService.deleteShipFishingPost(shipFishPostsId, user.getId());

		return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(true));
	}
}
package com.backend.domain.shipfishingpost.controller;

import java.net.URI;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import jakarta.validation.constraints.Min;
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

	@GetMapping("/mypage")
	@Operation(summary = "마이페이지 선상 낚시 게시글 목록", description = "유저가 본인이 작성한 선상 낚시 게시글을 조회할 때 사용하는 API")
	public ResponseEntity<GenericResponse<List<ShipFishingPostResponse.MyPagePostList>>> getMyPageShipFishingPostList(
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		List<ShipFishingPostResponse.MyPagePostList> response = shipFishingPostService
			.getMyPageShipFishingPostList(user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 상세 조회", description = "유저가 선상 낚시 게시글을 상세 조회할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "조회할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<ShipFishingPostResponse.DetailWithFileUrlAndFishName>> getShipFishingPost(
		@PathVariable("id") final Long shipFishingPostId
	) {

		ShipFishingPostResponse.DetailWithFileUrlAndFishName response = shipFishingPostService
			.getShipFishingPostAll(shipFishingPostId);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping
	@Operation(summary = "선상 낚시 게시글 검색 및 조회", description = "유저가 선상 낚시 게시글을 조회할 때 사용하는 API")
	public ResponseEntity<GenericResponse<ScrollResponse<ShipFishingPostResponse.DetailScroll>>> getShipFishingPostList(
		@ParameterObject @ModelAttribute final ShipFishingPostRequest.Search requestDto,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long memberId = user != null ? user.getId() : null;

		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostService
			.getShipFishingPostScroll(memberId, requestDto, cursorRequestDto);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping("/hot")
	@Operation(summary = "메인페이지 인기 선상 낚시", description = "메인페이지에 인기 선상낚시 목록을 조회할 때 사용하는 API")
	@Parameter(name = "size", required = true, description = "인기 선상낚시 목록 사이즈", example = "3")
	public ResponseEntity<GenericResponse<List<ShipFishingPostResponse.MainPageHotPost>>> getHotShipFishingPostList(
		@RequestParam @Min(1) final Integer size
	) {

		List<ShipFishingPostResponse.MainPageHotPost> response = shipFishingPostService
			.getMainPageHotShipFishingPostList(size);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@PatchMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 수정", description = "유저가 선상 낚시 게시글을 수정할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "수정할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<Long>> updateShipFishingPost(
		@PathVariable("id") final Long shipFishingPostId,
		@RequestBody @Valid final ShipFishingPostRequest.Update requestDto,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		Long responseShipFishingPostId = shipFishingPostService
			.updateShipFishingPost(shipFishingPostId, requestDto, user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, responseShipFishingPostId));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 삭제", description = "유저가 선상 낚시 게시글을 삭제할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "삭제할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<Void>> deleteShipFishingPost(
		@PathVariable("id") final Long shipFishingPostId,
		@AuthenticationPrincipal final CustomOAuth2User user
	) {

		shipFishingPostService.deleteShipFishingPost(shipFishingPostId, user.getId());

		return ResponseEntity.status(HttpStatus.OK).body(GenericResponse.of(true));
	}
}
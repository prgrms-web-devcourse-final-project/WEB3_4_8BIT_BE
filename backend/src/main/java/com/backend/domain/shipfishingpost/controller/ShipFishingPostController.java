package com.backend.domain.shipfishingpost.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.backend.global.response.GenericResponse;
import com.backend.global.util.pageutil.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "선상 낚시 게시글 API")
@RestController
@RequestMapping("/api/v1/ship-posts")
@RequiredArgsConstructor
public class ShipFishingPostController {

	private final ShipFishingPostService shipFishingPostService;

	@PostMapping
	@Operation(summary = "선상 낚시 게시글 생성", description = "유저가 새로운 선상 낚시 게시글을 생성할 때 사용하는 API")
	public ResponseEntity<GenericResponse<Void>> createShipFishPost(
		@RequestBody @Valid final ShipFishingPostRequest.Create requestDto,
		@AuthenticationPrincipal final CustomOAuth2User userDetails
	) {

		Long shipFishingPostId = shipFishingPostService.saveShipFishingPost(requestDto, userDetails.getId());

		return ResponseEntity.created(URI.create(shipFishingPostId.toString())).body(GenericResponse.of(true));
	}

	@GetMapping("/{id}")
	@Operation(summary = "선상 낚시 게시글 상세 조회", description = "유저가 선상 낚시 게시글을 상세 조회할 때 사용하는 API")
	@Parameter(name = "id", required = true, description = "조회할 선상 낚시 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<ShipFishingPostResponse.DetailAll>> getShipFishPost(
		@PathVariable("id") final Long shipFishPostsId
	) {

		ShipFishingPostResponse.DetailAll response = shipFishingPostService.getShipFishingPostAll(shipFishPostsId);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

	@GetMapping
	@Operation(summary = "선상 낚시 게시글 조회", description = "유저가 선상 낚시 게시글을 조회할 때 사용하는 API")
	@Parameter(name = "minPrice", description = "최소 가격", example = "10000")
	@Parameter(name = "maxPrice", description = "최대 가격", example = "500000")
	@Parameter(name = "minRating", description = "최소 평점", example = "4.5")
	@Parameter(name = "location", description = "위치 정보", example = "서울")
	@Parameter(name = "fishId", description = "물고기 ID", example = "1")
	@Parameter(name = "searchDate", description = "검색 날짜 (ISO 날짜 형식, 예: 2025-04-01)", example = "2025-04-01")
	@Parameter(name = "keyword", description = "검색 키워드", example = "낚시")
	@Parameter(name = "guestCount", description = "게스트 수", example = "2")
	@Parameter(name = "duration", description = "이용 시간 (ISO 시간 형식, 예: 01:30)", example = "01:30")
	@Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
	@Parameter(name = "size", description = "페이지당 항목 수", example = "6")
	@Parameter(name = "sort", description = "정렬 조건 (예: createdAt,desc)", example = "createdAt,desc")
	public ResponseEntity<GenericResponse<Page<ShipFishingPostResponse.DetailPage>>> getShipFishPostList(
		@RequestParam(required = false) final Long minPrice,
		@RequestParam(required = false) final Long maxPrice,
		@RequestParam(required = false) final Double minRating,
		@RequestParam(required = false) final String location,
		@RequestParam(required = false) final Long fishId,
		@RequestParam(required = false) final @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
		@RequestParam(required = false) final String keyword,
		@RequestParam(required = false) final Long guestCount,
		@RequestParam(required = false) final @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime duration,
		@PageableDefault(size = 6,
			page = 0,
			sort = "createdAt",
			direction = Sort.Direction.DESC) Pageable pageable
	) {

		ShipFishingPostRequest.Search requestDto = ShipFishingPostRequest.Search.builder()
			.keyword(keyword)
			.guestCount(guestCount)
			.searchDate(searchDate)
			.minPrice(minPrice)
			.maxPrice(maxPrice)
			.minRating(minRating)
			.location(location)
			.duration(duration)
			.fishId(fishId)
			.build();

		Page<ShipFishingPostResponse.DetailPage> response = shipFishingPostService
			.getShipFishingPostPage(requestDto, pageable);

		return ResponseEntity.ok(GenericResponse.of(true, response));
	}
}
package com.backend.domain.fishingtrippost.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.service.FishingTripPostService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.dto.response.ScrollResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "동출 모집 API")
@RequestMapping("/api/v1/fishing-trip-post")
public class FishingTripPostController {

	private final FishingTripPostService fishingTripPostService;

	@PostMapping
	@Operation(summary = "동출 모집 게시글 생성", description = "로그인한 사용자가 동출 모집 게시글 작성시 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> createFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestBody @Valid final FishingTripPostRequest.Create requestDto
	) {

		Long saveFishingTripPostId = fishingTripPostService.createFishingTripPost(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(saveFishingTripPostId.toString()))
			.body(GenericResponse.of(true));
	}

	@PatchMapping("/{fishingTripPostId}")
	@Operation(summary = "동출 모집 게시글 수정", description = "로그인한 사용자가 동출 모집 게시글 수정시 사용하는 API")
	public ResponseEntity<GenericResponse<Long>> updateFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@PathVariable final Long fishingTripPostId,
		@RequestBody @Valid final FishingTripPostRequest.Update requestDto
	) {

		Long updateFishingTripPostId = fishingTripPostService.updateFishingTripPost(
			user.getId(),
			fishingTripPostId,
			requestDto
		);

		return ResponseEntity.ok(GenericResponse.of(true, updateFishingTripPostId));
	}

	@GetMapping
	@Operation(summary = "동출 모집 게시글 상세조회", description = "동출 모집 게시글을 상세 조회하는 API")
	@Parameter(name = "id", required = true, description = "조회할 동출 모집 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<FishingTripPostResponse.Detail>> getFishingTripPostDetail(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestParam("id") final Long fishingTripPostId
	) {
		Long memberId = user != null ? user.getId() : null;
		FishingTripPostResponse.Detail responseDto = fishingTripPostService.getFishingTripPostDetail(
			memberId, fishingTripPostId);
		return ResponseEntity.ok().body(GenericResponse.of(true, responseDto));
	}

	@PatchMapping("/{fishingTripPostId}/completed")
	@Operation(summary = "동출 모집 게시글 모집완료", description = "로그인한 사용자가 동출 모집 게시글 모집완료하는 API")
	@Parameter(name = "fishingTripPostId", required = true, description = "조회할 동출 모집 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<Void>> completeFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@PathVariable final Long fishingTripPostId
	) {
		fishingTripPostService.completeFishingTripPost(user.getId(), fishingTripPostId);

		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@GetMapping("/scroll")
	@Operation(summary = "동출 모집 게시글 스크롤 조회", description = "커서 기반으로 동출 모집 게시글 목록을 조회하는 API")
	@Parameter(name = "regionId", description = "지역 ID", example = "2")
	@Parameter(name = "keyword", description = "제목 키워드 검색", example = "해적")
	@Parameter(name = "status", description = "게시글 상태 (예: RECRUITING, COMPLETED)", example = "RECRUITING")
	public ResponseEntity<GenericResponse<ScrollResponse<FishingTripPostResponse.DetailPage>>> getFishingTripPostPages(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@RequestParam(required = false) final PostStatus status,
		@RequestParam(required = false) final Long regionId,
		@RequestParam(required = false) final String keyword
	) {
		ScrollResponse<FishingTripPostResponse.DetailPage> responseDto =
			fishingTripPostService.getDetailPage(cursorRequestDto, user.getId(), status, regionId, keyword);

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@GetMapping("/participation")
	@Operation(
		summary = "동출 모집 게시글 참여자 정보 조회",
		description = "해당 게시글에 대해 현재 로그인한 사용자의 신청 여부, 작성자 여부, 참여자 목록을 포함한 정보를 조회하는 api"
	)
	@Parameter(name = "fishingTripPostId", required = true, description = "조회할 동출 모집 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<FishingTripPostResponse.FishingTripPostParticipationDetail>> getParticipationDetail(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@RequestParam final Long fishingTripPostId
	) {
		Long memberId = user != null ? user.getId() : null;
		FishingTripPostResponse.FishingTripPostParticipationDetail responseDto =
			fishingTripPostService.getFishingTripPostParticipationDetail(memberId, fishingTripPostId);

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@DeleteMapping("/{fishingTripPostId}")
	@Operation(
		summary = "동출 모집 게시글 삭제",
		description = "로그인한 사용자가 자신이 작성한 동출 모집 게시글을 삭제하는 API"
	)
	@Parameter(name = "fishingTripPostId", required = true, description = "삭제할 동출 모집 게시글 ID", example = "1")
	public ResponseEntity<GenericResponse<Void>> deleteFishingTripPost(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@PathVariable final Long fishingTripPostId
	) {
		fishingTripPostService.delete(user.getId(), fishingTripPostId);
		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@GetMapping("/my-participate")
	@Operation(
		summary = "내가 신청한 동출 모집글 스크롤 조회",
		description = "로그인한 사용자가 신청한 동출 모집글을 상태 기준으로 조회하는 API"
	)
	@Parameter(name = "status", required = true, description = "게시글 상태 ", example = "RECRUITING")
	public ResponseEntity<GenericResponse<ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage>>> getMyParticipatedFishingTripPosts(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@RequestParam final PostStatus status
	) {
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> responseDto =
			fishingTripPostService.getMyFishingTripPostDetailPage(cursorRequestDto, user.getId(), status);

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@GetMapping("/my-post")
	@Operation(
		summary = "내가 작성한 동출 모집글 스크롤 조회",
		description = "로그인한 사용자가 작성한 동출 모집글을 상태 기준으로 조회하는 API"
	)
	@Parameter(name = "status", required = true, description = "게시글 상태", example = "RECRUITING")
	public ResponseEntity<GenericResponse<ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage>>> getMyPostedFishingTripPosts(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid final GlobalRequest.CursorRequest cursorRequestDto,
		@RequestParam final PostStatus status
	) {
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> responseDto =
			fishingTripPostService.getMyPostFishingTripPostDetailPage(cursorRequestDto, user.getId(), status);

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@GetMapping("/hot-post")
	@Operation(
		summary = "HOT 동출 모집글 조회",
		description = "최근 5일 내 작성된 동출 모집글 중 댓글 + 좋아요 수를 기준으로 인기글 상위 5개를 조회하는 api"
	)
	public ResponseEntity<GenericResponse<List<FishingTripPostResponse.HotPost>>> getHotFishingTripPosts() {
		List<FishingTripPostResponse.HotPost> response = fishingTripPostService.getHotPost();
		return ResponseEntity.ok(GenericResponse.of(true, response));
	}

}

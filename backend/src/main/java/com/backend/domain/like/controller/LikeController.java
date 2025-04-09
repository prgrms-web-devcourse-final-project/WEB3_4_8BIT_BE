package com.backend.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.service.LikeService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name = "좋아요 API", description = "좋아요 토글 관련 API")
public class LikeController {

	private final LikeService likeService;

	@Operation(summary = "좋아요 토글", description = "좋아요를 누르거나 취소할 수 있는 API입니다.")
	@PostMapping("/toggle")
	public ResponseEntity<GenericResponse<Void>> toggleLike(
		@AuthenticationPrincipal final CustomOAuth2User user,
		@Valid @RequestBody LikeRequest requestDto
	) {
		likeService.toggleLike(user.getId(), requestDto);
		return ResponseEntity.ok(GenericResponse.of(true));
	}
}

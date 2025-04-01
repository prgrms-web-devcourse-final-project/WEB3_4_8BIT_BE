package com.backend.domain.member.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관련 API")
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping
	@Operation(summary = "추가 회원 정보 입력", description = "회원가입 후에 추가정보를 입력받는 API")
	public ResponseEntity<GenericResponse<Void>> createAddInfo(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody @Valid MemberRequest.Form requestDto) {

		Long memberId = memberService.saveAddInfo(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(memberId.toString())).body(GenericResponse.of(true));
	}

	@GetMapping
	@Operation(summary = "회원 정보 조회", description = "현재 로그인된 사용자의 회원 정보를 조회하는 API")
	public ResponseEntity<GenericResponse<MemberResponse.Detail>> getMemberDetail(
		@AuthenticationPrincipal CustomOAuth2User user) {

		MemberResponse.Detail responseDto = memberService.getMemberDetail(user.getId());

		return ResponseEntity.ok(GenericResponse.of(true, responseDto));
	}

	@PatchMapping
	@Operation(summary = "회원 정보 수정", description = "현재 로그인된 사용자의 회원 정보를 수정하는 API")
	public ResponseEntity<GenericResponse<Void>> updateMember(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody @Valid MemberRequest.Form requestDto) {

		Long memberId = memberService.updateMember(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(memberId.toString())).body(GenericResponse.of(true));
	}
}

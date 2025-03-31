package com.backend.domain.member.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관련 API")
@RequestMapping("/api/v1/member")
public class MemberController {

	private final MemberService memberService;

	@PostMapping
	@Operation(summary = "추가 회원 정보 입력", description = "회원가입 후에 추가정보를 입력받는 API")
	public ResponseEntity<GenericResponse<Void>> createAddInfo(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody @Valid MemberRequest.Create requestDto) {

		Long memberId = memberService.saveAddInfo(user.getId(), requestDto);

		return ResponseEntity.created(URI.create(memberId.toString())).body(GenericResponse.of(true));
	}
}

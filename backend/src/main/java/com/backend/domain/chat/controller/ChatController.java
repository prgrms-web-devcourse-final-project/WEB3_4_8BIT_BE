package com.backend.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.CursorResponse;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.service.MessageService;
import com.backend.domain.chat.room.service.RoomService;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.dto.response.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅방 API")
@RequestMapping("/api/v1/chats")
public class ChatController {

	private	final RoomService roomService;
	private final MessageService messageService;

	@GetMapping
	@Operation(summary = "채팅방 목록 조회", description = "로그인 한 회원의 채팅방 목록을 조회하는 API")
	public ResponseEntity<GenericResponse<Void>> getRooms(
		@AuthenticationPrincipal final CustomOAuth2User user
	) {
		//TODO 채팅방 목록 조회
		return ResponseEntity.ok(GenericResponse.of(true));
	}

	@GetMapping("/{roomId}/messages")
	@Operation(summary = "메세지 조회", description = "해당 채팅방의 이전 메세지를 조회하는 API")
	public ResponseEntity<GenericResponse<CursorResponse<MessageResponse.Basic>>> getRoomDetail(
		@PathVariable final Long roomId,
		@Valid final ChatRequest.MessageCursorRequest cursorRequestDto
	) {
		CursorResponse<MessageResponse.Basic> messageScrollResponse = messageService.getMessagesByRoomId(roomId, cursorRequestDto);

		return ResponseEntity.ok(GenericResponse.of(true, messageScrollResponse));
	}
}

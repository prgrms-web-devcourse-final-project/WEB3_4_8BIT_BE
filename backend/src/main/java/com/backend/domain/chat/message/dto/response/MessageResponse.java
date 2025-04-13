package com.backend.domain.chat.message.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.domain.chat.message.entity.MessageType;

import lombok.Builder;

public class MessageResponse {

	@Builder
	public record Basic(
		String messageId,
		Long roomId,
		Long senderId,
		String senderNickname,
		String senderProfileImageUrl,
		String content,
		List<String> fileUrls,
		MessageType type,
		boolean isMine,
		LocalDateTime createdAt
	) {}

	public record Last(
		String content,
		MessageType type,
		String senderNickname,
		LocalDateTime createdAt
	) {}
}

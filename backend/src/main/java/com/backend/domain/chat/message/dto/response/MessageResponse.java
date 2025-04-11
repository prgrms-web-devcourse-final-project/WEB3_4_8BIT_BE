package com.backend.domain.chat.message.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.backend.domain.chat.message.entity.MessageType;

import lombok.Builder;

@Builder
public record MessageResponse(
	String messageId,
	Long roomId,
	Long senderId,
	String senderNickname,
	String senderProfileImageUrl,
	String content,
	List<String> fileUrls,
	MessageType type,
	ZonedDateTime createdAt
) {}

package com.backend.domain.chat.message.dto.request;

import java.util.List;

import com.backend.domain.chat.message.entity.MessageType;

public record MessageRequest(
	Long roomId,
	Long senderId,
	String senderNickname,
	String content,
	List<Long> fileIds,
	MessageType type
) {}

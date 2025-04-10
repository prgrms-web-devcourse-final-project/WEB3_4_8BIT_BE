package com.backend.domain.chat.message.converter;

import java.util.List;

import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.entity.Message;

public class MessageConverter {

	public static Message toEntity(
		final Long senderId,
		final String nickname,
		final MessageRequest request,
		final List<String> fileUrls
	){
		return Message.builder()
			.roomId(request.roomId())
			.senderId(senderId)
			.senderNickname(nickname)
			.content(request.content())
			.fileIds(request.fileIds())
			.fileUrls(fileUrls)
			.type(request.type())
			.disabled(false)
			.build();
	}

	public static MessageResponse toResponse(final Message message, final String senderProfileImageUrl) {
		return MessageResponse.builder()
			.messageId(message.getMessageId().toHexString())
			.roomId(message.getRoomId())
			.senderId(message.getSenderId())
			.senderNickname(message.getSenderNickname())
			.senderProfileImageUrl(senderProfileImageUrl)
			.content(message.getContent())
			.fileUrls(message.getFileUrls())
			.type(message.getType())
			.createdAt(message.getCreatedAt())
			.build();
	}
}

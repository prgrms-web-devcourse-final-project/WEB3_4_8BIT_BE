package com.backend.domain.chat.message.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "messages")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

	@Id
	@Field("_id")
	private ObjectId messageId;

	@Field("room_id")
	@Indexed
	private Long roomId;

	@Field("sender_id")
	private Long senderId;

	@Field("sender_nickname")
	private String senderNickname;

	@Field("content")
	private String content;

	@Field("file_ids")
	private List<Long> fileIds;

	@Field("file_urls")
	private List<String> fileUrls;

	@Field("type")
	private MessageType type;

	@Field("disabled")
	private Boolean disabled;

	@CreatedDate
	@Field("createdAt")
	private LocalDateTime createdAt;

	/**
	 * 메세지 활성화 (디폴트 값)
	 */
	public void activate() {
		disabled = false;
	}

	/**
	 * 메세지 삭제
	 */
	public void deactivate() {
		disabled = true;
	}

}

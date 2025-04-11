package com.backend.domain.chat.message.repository;

import com.backend.domain.chat.message.entity.Message;

public interface MessageRepository {

	/**
	 * 주어진 메시지를 저장
	 *
	 * @param message 저장할 {@link Message} 객체
	 * @return 저장된 {@link Message} 객체
	 */
	Message save(final Message message);
}

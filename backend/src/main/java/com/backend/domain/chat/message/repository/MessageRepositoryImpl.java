package com.backend.domain.chat.message.repository;

import org.springframework.stereotype.Repository;

import com.backend.domain.chat.message.entity.Message;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

	private final MessageMongoRepository messageMongoRepository;
	private final MessageQueryRepository messageQueryRepository;

	@Override
	public Message save(final Message message) {
		return messageMongoRepository.save(message);
	}
}

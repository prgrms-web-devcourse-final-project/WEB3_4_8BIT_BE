package com.backend.domain.chat.message.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.backend.domain.chat.message.entity.Message;

public interface MessageMongoRepository extends MongoRepository<Message, String> {
}

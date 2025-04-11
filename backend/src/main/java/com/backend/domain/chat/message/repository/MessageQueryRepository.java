package com.backend.domain.chat.message.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {

	private final MongoTemplate mongoTemplate;
}

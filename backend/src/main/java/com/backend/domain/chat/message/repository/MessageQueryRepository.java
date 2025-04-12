package com.backend.domain.chat.message.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.message.entity.Message;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepository {

	private final MongoTemplate mongoTemplate;

	public List<Message> findMessagesByRoomId(final Long roomId, final ChatRequest.MessageCursorRequest cursorRequestDto) {
		Criteria criteria = Criteria.where("room_id").is(roomId);

		// 커서 조건 (_id 기준으로만)
		if (cursorRequestDto != null) {
			criteria = criteria.and("_id").lt(new ObjectId(cursorRequestDto.id()));
		}

		Query query = new Query(criteria)
			.with(Sort.by(Sort.Direction.DESC, "_id"))
			.limit(cursorRequestDto.size());

		return mongoTemplate.find(query, Message.class);
	}

	public Map<Long, Message> findLastMessagesByRoomIds(final List<Long> roomIdList) {
		if (roomIdList == null || roomIdList.isEmpty()) {
			return Map.of();
		}

		Map<Long, Message> resultMap = new HashMap<>();

		for (Long roomId : roomIdList) {
			Query query = new Query(Criteria.where("room_id").is(roomId))
				.with(Sort.by(Sort.Direction.DESC, "_id"))
				.limit(1);

			Message message = mongoTemplate.findOne(query, Message.class);
			if (message != null) {
				resultMap.put(roomId, message);
			}
		}
		return resultMap;
	}
}

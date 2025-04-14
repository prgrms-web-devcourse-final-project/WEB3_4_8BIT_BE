package com.backend.domain.chat.room.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.chat.message.converter.MessageConverter;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.entity.Message;
import com.backend.domain.chat.message.repository.MessageRepository;
import com.backend.domain.chat.room.converter.RoomConverter;
import com.backend.domain.chat.room.dto.response.RoomResponse;
import com.backend.domain.chat.room.entity.Room;
import com.backend.domain.chat.room.entity.TargetType;
import com.backend.domain.chat.room.repository.RoomRepository;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;
	private final MessageRepository messageRepository;
	private final FishingTripPostRepository fishingTripPostRepository;
	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;

	@Override
	@Transactional
	public Long createRoom(final Long targetId, final String targetName, final TargetType targetType) {
		Room room = RoomConverter.fromTargetIdAndTargetType(targetId, targetName, targetType);
		return roomRepository.save(room).getRoomId();
	}

	@Async
	@Override
	@Transactional
	public void updateLastMessageTime(final Long roomId, final ZonedDateTime lastMessageTime) {
		roomRepository.updateLastMessageTime(roomId, lastMessageTime);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoomResponse.Basic> getRoomList(final Long memberId) {
		// 1. 내가 작성한 게시글 및 참여자 수 조회
		Map<Long, Integer> authoredPostCountMap =
			fishingTripPostRepository.findFishingTripPostIdWithApprovedCount(memberId);

		// 2. 내가 참여한 게시글 및 참여자 수 조회
		Map<Long, Integer> joinedPostCountMap =
			fishingTripRecruitmentRepository.findApprovedFishingTripPostIdsWithCount(memberId);

		// 3. 참여자 수 병합 (작성자 포함)
		Map<Long, Integer> postIdToParticipantCount = new HashMap<>();
		authoredPostCountMap.forEach((postId, count) -> postIdToParticipantCount.put(postId, count + 1));
		joinedPostCountMap.forEach((postId, count) -> postIdToParticipantCount.putIfAbsent(postId, count + 1));

		// 4. 채팅방 목록 조회
		List<Room> rooms = roomRepository.findRoomsByIds(new ArrayList<>(postIdToParticipantCount.keySet()));

		// 5. 마지막 메시지 조회
		Map<Long, Message> lastMessageByRoomIds = messageRepository.findLastMessageByRoomIds(
			rooms.stream().map(Room::getRoomId).toList());

		// 6. 응답 DTO 변환
		return rooms.stream()
			.map(room -> {
				MessageResponse.Last lastMessage = Optional.ofNullable(lastMessageByRoomIds.get(room.getRoomId()))
					.map(MessageConverter::toLastMessageResponse)
					.orElse(null);

				int participantCount = postIdToParticipantCount.getOrDefault(room.getTargetId(), 0);

				return new RoomResponse.Basic(
					room.getRoomId(),
					room.getTargetId(),
					room.getTargetName(),
					room.getTargetType(),
					participantCount,
					lastMessage
				);
			})
			.toList();
	}
}

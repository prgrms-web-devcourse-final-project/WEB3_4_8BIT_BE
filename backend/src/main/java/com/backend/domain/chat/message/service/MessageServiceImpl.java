package com.backend.domain.chat.message.service;

import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.message.converter.MessageConverter;
import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.entity.Message;
import com.backend.domain.chat.message.repository.MessageQueryRepository;
import com.backend.domain.chat.message.repository.MessageRepository;
import com.backend.domain.chat.room.service.RoomService;
import com.backend.domain.member.service.MemberService;
import com.backend.global.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final StorageService storageService;
	private final MessageQueryRepository messageQueryRepository;
	private final MemberService memberService;
	private final RoomService roomService;

	@Override
	public MessageResponse.Basic saveMessage(
		final Long senderId,
		final String nickname,
		final String fileUrl,
		final MessageRequest request
	) {
		// TODO 이거 고민해 봐야함. 현재는 방법이 생각이 안남. 비동기 처리를 어떻게 할 수 있을지?
		// 1. 파일 URL 조회 (파일 ID가 있을 때만)
		List<String> fileUrls = request.fileIds() == null || request.fileIds().isEmpty()
			? List.of()
			: storageService.getFileUrlsByIdList(request.fileIds());

		// 2. Message 도큐먼트 생성
		Message message = MessageConverter.toEntity(senderId, nickname, request, fileUrls);

		// 3. 저장
		Message saved = messageRepository.save(message);

		// 4. 채팅방 마지막 메세지 시간 비동기 업데이트
		roomService.updateLastMessageTime(saved.getRoomId(), saved.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")));

		// 5. 응답 객체로 변환 후 반환
		return MessageConverter.toResponse(saved, fileUrl);
	}

	@Override
	public ChatResponse.MessageCursorResponse<MessageResponse.Basic> getMessagesByRoomId(
		final Long roomId,
		final ChatRequest.MessageCursorRequest cursorRequestDto
	) {
		// 1. 메시지 목록 조회
		List<Message> messageList = messageQueryRepository.findMessagesByRoomId(roomId, cursorRequestDto);

		// 2. Message → MessageResponse 변환 (getChatProfile 호출)
		List<MessageResponse.Basic> responseList = messageList.stream()
			.map(message -> {
				String profileImageUrl = memberService.getChatProfile(message.getSenderId()).fileUrl();
				return MessageConverter.toResponse(message, profileImageUrl);
			})
			.toList();

		// 3. 다음 커서 설정
		String nextCursorId = messageList.isEmpty() ? null
			: messageList.get(messageList.size() - 1).getMessageId().toString();

		// 4. 커서 응답 생성
		return ChatResponse.MessageCursorResponse.of(responseList, nextCursorId);
	}
}

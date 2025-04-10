package com.backend.domain.chat.message.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.chat.message.converter.MessageConverter;
import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.entity.Message;
import com.backend.domain.chat.message.repository.MessageRepository;
import com.backend.global.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final StorageService storageService;

	@Override
	public MessageResponse saveMessage(
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

		// 5. 응답 객체로 변환 후 반환
		return MessageConverter.toResponse(saved, fileUrl);
	}
}

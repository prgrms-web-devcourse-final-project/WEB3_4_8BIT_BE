package com.backend.global.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Override
	@Async("threadPoolTaskExecutor")
	public void sendFishingTripPostEmail(final String subject, final String receiver) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(receiver);
			message.setSubject("[" + subject + "] 신청 완료 안내");
			message.setText("신청하신 낚시 게시글의 모집이 마감 되었습니다. 즐거운 낚시여행 되세요");

			mailSender.send(message);
		} catch (Exception e) {
			log.error("[EmailService] 메일 전송 실패 - subject: {}, receiver: {}, error: {}", subject, receiver, e.getMessage(), e);
		}
	}
}
